package codea.uni.desafio_fullstack.operations.application.internal.commandservices;

import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.commands.*;
import codea.uni.desafio_fullstack.operations.domain.model.exceptions.AssignmentValidationError;
import codea.uni.desafio_fullstack.operations.domain.model.exceptions.AssignmentValidationException;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentCommandService;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssignmentCommandServiceImpl implements AssignmentCommandService {

    private final AssignmentRepository assignmentRepository;
    private final ShiftRepository shiftRepository;
    private final ExternalMachineryService externalMachineryService;
    private final ExternalOperatorService externalOperatorService;

    public AssignmentCommandServiceImpl(
            AssignmentRepository assignmentRepository,
            ShiftRepository shiftRepository,
            ExternalMachineryService externalMachineryService,
            ExternalOperatorService externalOperatorService) {
        this.assignmentRepository = assignmentRepository;
        this.shiftRepository = shiftRepository;
        this.externalMachineryService = externalMachineryService;
        this.externalOperatorService = externalOperatorService;
    }

    @Override
    public Optional<Assignment> handle(CreateAssignmentCommand command) {
        validateForeignReferences(command.operatorId(), command.machineryCode(), command.shiftId());

        List<AssignmentValidationError> errors = new ArrayList<>();

        //Operator on same shift exists
        if(this.assignmentRepository.existsByShiftIdAndOperatorId(command.shiftId(), command.operatorId())) {
            errors.add(new AssignmentValidationError(
                    "OPERATOR_SHIFT_DUPLICATE",
                    "El operador ya está asignado a este turno"
            ));
        }

        //Equipment on same shift exists
        if(this.assignmentRepository.existsByShiftIdAndMachineryCode(command.shiftId(), command.machineryCode())) {
            errors.add(new AssignmentValidationError(
                    "MACHINERY_SHIFT_DUPLICATE",
                    "La maquinaria ya está asignada a este turno"
            ));
        }
        //Blocked equipment exception
        if(!this.externalMachineryService.isMachineryActive(command.machineryCode())){
            errors.add(new AssignmentValidationError(
                    "EQUIPMENT_BLOCKED",
                    "El equipo con codigo " + command.machineryCode() + " no está activo"
            ));
        }

        //Equipment Certification exception
        var machineryType = externalMachineryService.getMachineryTypeId(command.machineryCode());
        if(!this.externalOperatorService.isOperatorCertifiedForMachineryType(command.operatorId(), machineryType, LocalDate.now())){
            errors.add(new AssignmentValidationError(
                    "NO_CERTIFICATION",
                    "La certificación para la maquinaria de tipo " + machineryType + " no existe o no está activa"
            ));
        }

        if(!errors.isEmpty()){
            throw new AssignmentValidationException(errors);
        }

        var assignment = new Assignment(command);
        var savedAssignment = this.assignmentRepository.save(assignment);
        return Optional.of(savedAssignment);
    }

    @Override
    public Optional<Assignment> handle(UpdateAssignmentCommand command) {
        var assignmentOptional = this.assignmentRepository.findById(command.id());
        if (assignmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Assignment with id " + command.id() + " does not exist");
        }

        validateForeignReferences(command.operatorId(), command.machineryCode(), command.shiftId());

        var assignment = assignmentOptional.get();
        assignment.updateDetails(command.operatorId(), command.machineryCode(), command.shiftId());
        var updatedAssignment = this.assignmentRepository.save(assignment);
        return Optional.of(updatedAssignment);
    }

    @Override
    public Optional<Assignment> handle(StartAssignmentCommand command) {
        var assignmentOptional = this.assignmentRepository.findById(command.id());
        if (assignmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Assignment with id " + command.id() + " does not exist");
        }

        var assignment = assignmentOptional.get();
        LocalDateTime resolvedStart;
        if (command.dateTimeStart() != null) {
            resolvedStart = command.dateTimeStart();
        } else if (command.timeStart() != null) {
            var shift = this.shiftRepository.findById(assignment.getShiftId())
                    .orElseThrow(() -> new IllegalArgumentException("Shift with id " + assignment.getShiftId() + " does not exist"));
            resolvedStart = shift.getDate().atTime(command.timeStart());
        } else {
            resolvedStart = LocalDateTime.now();
        }

        assignment.startShift(resolvedStart);
        var updatedAssignment = this.assignmentRepository.save(assignment);
        return Optional.of(updatedAssignment);
    }

    @Override
    public Optional<Assignment> handle(EndAssignmentCommand command) {
        var assignmentOptional = this.assignmentRepository.findById(command.id());
        if (assignmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Assignment with id " + command.id() + " does not exist");
        }

        var assignment = assignmentOptional.get();
        LocalDateTime resolvedEnd;
        if (command.dateTimeEnd() != null) {
            resolvedEnd = command.dateTimeEnd();
        } else if (command.timeEnd() != null) {
            var shift = this.shiftRepository.findById(assignment.getShiftId())
                    .orElseThrow(() -> new IllegalArgumentException("Shift with id " + assignment.getShiftId() + " does not exist"));
            LocalDate baseDate = (assignment.getTimeStart() != null)
                    ? assignment.getTimeStart().toLocalDate()
                    : shift.getDate();

            if (assignment.getTimeStart() != null && command.timeEnd().isBefore(assignment.getTimeStart().toLocalTime())) {
                baseDate = baseDate.plusDays(1);
            }
            resolvedEnd = baseDate.atTime(command.timeEnd());
        } else {
            resolvedEnd = LocalDateTime.now();
        }

        assignment.endShift(resolvedEnd);

        if (assignment.getActualShiftTime() != null && assignment.getActualShiftTime() > 0) {
            this.externalMachineryService.recordWorkedHours(assignment.getMachineryCode(), (float) assignment.getActualShiftTime());
        }

        var updatedAssignment = this.assignmentRepository.save(assignment);
        return Optional.of(updatedAssignment);
    }

    @Override
    public void handle(DeleteAssignmentCommand command) {
        var assignmentOptional = this.assignmentRepository.findById(command.id());
        if (assignmentOptional.isEmpty()) {
            throw new IllegalArgumentException("Assignment with id " + command.id() + " does not exist");
        }
        this.assignmentRepository.delete(assignmentOptional.get());
    }

    private void validateForeignReferences(java.util.UUID operatorId, String machineryCode, java.util.UUID shiftId) {
        if (!this.externalOperatorService.existsOperatorById(operatorId)) {
            throw new IllegalArgumentException("Operator with id " + operatorId + " does not exist");
        }
        if (!this.externalMachineryService.existsMachineryByCode(machineryCode)) {
            throw new IllegalArgumentException("Machinery with code " + machineryCode + " does not exist");
        }
        if (!this.shiftRepository.existsById(shiftId)) {
            throw new IllegalArgumentException("Shift with id " + shiftId + " does not exist");
        }
    }
}
