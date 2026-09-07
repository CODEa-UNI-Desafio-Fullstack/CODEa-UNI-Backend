package codea.uni.desafio_fullstack.operations;

import codea.uni.desafio_fullstack.operations.application.internal.commandservices.AssignmentCommandServiceImpl;
import codea.uni.desafio_fullstack.operations.application.internal.commandservices.ShiftCommandServiceImpl;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.*;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftAndAssignmentIntegrationTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @Mock
    private ExternalOperatorService externalOperatorService;

    private ShiftCommandServiceImpl shiftCommandService;
    private AssignmentCommandServiceImpl assignmentCommandService;

    @BeforeEach
    void setUp() {
        shiftCommandService = new ShiftCommandServiceImpl(shiftRepository);
        assignmentCommandService = new AssignmentCommandServiceImpl(
                assignmentRepository,
                shiftRepository,
                externalMachineryService,
                externalOperatorService
        );
    }

    @Test
    @DisplayName("Should execute complete joint workflow: create shift, create assignment, start, end and calculate hours")
    void shouldExecuteCompleteJointWorkflow() {
        // 1. Create Shift
        LocalDate shiftDate = LocalDate.of(2026, 9, 7);
        CreateShiftCommand createShiftCommand = new CreateShiftCommand(shiftDate, true, 8);
        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        Optional<Shift> shiftOptional = shiftCommandService.handle(createShiftCommand);
        assertTrue(shiftOptional.isPresent());
        Shift createdShift = shiftOptional.get();
        UUID shiftId = createdShift.getId();

        // 2. Create Assignment for this Shift
        UUID operatorId = UUID.randomUUID();
        String machineryCode = "EXC-001";
        when(externalOperatorService.existsOperatorById(operatorId)).thenReturn(true);
        when(externalMachineryService.existsMachineryByCode(machineryCode)).thenReturn(true);
        when(shiftRepository.existsById(shiftId)).thenReturn(true);

        when(assignmentRepository.save(any(Assignment.class))).thenAnswer(inv -> {
            Assignment a = inv.getArgument(0);
            if (a.getId() == null) {
                a.setId(UUID.randomUUID());
            }
            return a;
        });

        CreateAssignmentCommand createAssignmentCommand = new CreateAssignmentCommand(operatorId, machineryCode, shiftId);
        Optional<Assignment> assignmentOptional = assignmentCommandService.handle(createAssignmentCommand);

        assertTrue(assignmentOptional.isPresent());
        Assignment assignment = assignmentOptional.get();
        UUID assignmentId = assignment.getId();
        assertNull(assignment.getTimeStart());
        assertNull(assignment.getTimeEnd());
        assertNull(assignment.getActualShiftTime());

        // 3. Start Shift on Assignment
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 7, 7, 0);
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        StartAssignmentCommand startCommand = new StartAssignmentCommand(assignmentId, startTime);
        Optional<Assignment> startedOptional = assignmentCommandService.handle(startCommand);

        assertTrue(startedOptional.isPresent());
        assertEquals(startTime, startedOptional.get().getTimeStart());

        // 4. End Shift on Assignment (worked 8 hours: 07:00 to 15:00)
        LocalDateTime endTime = LocalDateTime.of(2026, 9, 7, 15, 0);
        EndAssignmentCommand endCommand = new EndAssignmentCommand(assignmentId, endTime);
        Optional<Assignment> endedOptional = assignmentCommandService.handle(endCommand);

        assertTrue(endedOptional.isPresent());
        assertEquals(endTime, endedOptional.get().getTimeEnd());
        assertEquals(8, endedOptional.get().getActualShiftTime());

        // 5. Delete Assignment & Delete Shift
        assignmentCommandService.handle(new DeleteAssignmentCommand(assignmentId));
        verify(assignmentRepository, times(1)).delete(assignment);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(createdShift));
        shiftCommandService.handle(new DeleteShiftCommand(shiftId));
        verify(shiftRepository, times(1)).delete(createdShift);
    }
}
