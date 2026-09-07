package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetMachineryMaintenanceProjectionsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.AssignmentDetail;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.MachineryMaintenanceProjection;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentQueryService;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;
    private final ShiftRepository shiftRepository;
    private final ExternalMachineryService externalMachineryService;
    private final ExternalOperatorService externalOperatorService;

    public AssignmentQueryServiceImpl(
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
    public Optional<Assignment> handle(GetAssignmentByIdQuery query) {
        return this.assignmentRepository.findById(query.id());
    }

    @Override
    public List<MachineryMaintenanceProjection> handle(GetMachineryMaintenanceProjectionsQuery query) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);

        // Retrieve all machineries via ACL
        List<MachinerySummaryRecord> allMachineries = this.externalMachineryService.getAllMachineriesForProjection();
        if (allMachineries == null || allMachineries.isEmpty()) {
            return List.of();
        }

        // Exclude machineries that are already blocked or inactive at search time
        var machineryStream = allMachineries.stream()
                .filter(m -> m.active() && !m.blocked());

        // Apply optional machineryCode filter
        if (query.machineryCode() != null && !query.machineryCode().isBlank()) {
            String codeFilter = query.machineryCode().trim().toLowerCase();
            machineryStream = machineryStream.filter(m -> m.code().toLowerCase().contains(codeFilter));
        }

        // Apply optional machineryType filter (by ID or name)
        if (query.machineryType() != null && !query.machineryType().isBlank()) {
            String typeFilter = query.machineryType().trim().toLowerCase();
            machineryStream = machineryStream.filter(m ->
                    (m.machineryTypeName() != null && m.machineryTypeName().toLowerCase().contains(typeFilter)) ||
                    (m.machineryTypeId() != null && m.machineryTypeId().toString().equals(typeFilter)));
        }

        List<MachinerySummaryRecord> candidateMachineries = machineryStream.toList();
        if (candidateMachineries.isEmpty()) {
            return List.of();
        }

        // Retrieve scheduled shifts within the 7-day window sorted chronologically
        List<Shift> futureShifts = this.shiftRepository.findShiftsBetweenDatesSorted(startDate, endDate);
        if (futureShifts == null || futureShifts.isEmpty()) {
            return List.of();
        }

        List<UUID> shiftIds = futureShifts.stream().map(Shift::getId).toList();
        Map<UUID, Shift> shiftMap = futureShifts.stream()
                .collect(Collectors.toMap(Shift::getId, Function.identity(), (s1, s2) -> s1));

        // Retrieve assignments for those shifts
        List<Assignment> futureAssignments = this.assignmentRepository.findAllByShiftIdIn(shiftIds);
        Map<String, List<Assignment>> assignmentsByMachinery = futureAssignments.stream()
                .collect(Collectors.groupingBy(Assignment::getMachineryCode));

        List<MachineryMaintenanceProjection> results = new ArrayList<>();

        for (MachinerySummaryRecord machinery : candidateMachineries) {
            float remainingHours = machinery.remainingHoursToMaintenance();

            List<Assignment> machAssignments = assignmentsByMachinery.getOrDefault(machinery.code(), List.of())
                    .stream()
                    .sorted(Comparator.comparing((Assignment a) -> {
                        Shift s = shiftMap.get(a.getShiftId());
                        return s != null ? s.getDate() : LocalDate.MAX;
                    }).thenComparing((Assignment a) -> {
                        Shift s = shiftMap.get(a.getShiftId());
                        return (s != null && Boolean.TRUE.equals(s.getShiftType())) ? 0 : 1;
                    }))
                    .toList();

            float projectedHours = 0.0f;
            LocalDate estimatedThresholdDate = null;
            UUID estimatedThresholdShiftId = null;
            String estimatedThresholdShiftType = null;
            boolean willReachThreshold = false;

            for (Assignment assignment : machAssignments) {
                Shift shift = shiftMap.get(assignment.getShiftId());
                if (shift != null && shift.getDuration() != null) {
                    projectedHours += shift.getDuration();
                    if (!willReachThreshold && projectedHours >= remainingHours) {
                        willReachThreshold = true;
                        estimatedThresholdDate = shift.getDate();
                        estimatedThresholdShiftId = shift.getId();
                        estimatedThresholdShiftType = Boolean.TRUE.equals(shift.getShiftType()) ? "Dia" : "Noche";
                    }
                }
            }

            // Only include machineries that are currently active but WILL be blocked in the 7-day window
            if (willReachThreshold) {
                float differenceHours = projectedHours - remainingHours;
                results.add(new MachineryMaintenanceProjection(
                        machinery.code(),
                        machinery.machineryTypeId(),
                        machinery.machineryTypeName(),
                        machinery.currentHourMeter(),
                        machinery.maintenanceThreshold(),
                        remainingHours,
                        projectedHours,
                        differenceHours,
                        estimatedThresholdDate,
                        estimatedThresholdShiftId,
                        estimatedThresholdShiftType
                ));
            }
        }

        return results;
    }

    @Override
    public List<AssignmentDetail> handle(GetAssignmentsByFilterQuery query) {
        List<Assignment> assignments = this.assignmentRepository.findAll();
        if (assignments.isEmpty()) {
            return List.of();
        }

        // Batch load shifts to avoid N+1 queries
        List<UUID> shiftIds = assignments.stream().map(Assignment::getShiftId).distinct().toList();
        Map<UUID, Shift> shiftMap = this.shiftRepository.findAllById(shiftIds).stream()
                .collect(Collectors.toMap(Shift::getId, Function.identity(), (s1, s2) -> s1));

        // Batch load operator summaries via ACL
        List<OperatorSummaryRecord> allOperators = this.externalOperatorService.getAllOperators();
        Map<UUID, String> operatorNameMap = (allOperators != null) ? allOperators.stream()
                .collect(Collectors.toMap(OperatorSummaryRecord::id, OperatorSummaryRecord::name, (o1, o2) -> o1))
                : Map.of();

        // Batch load machinery summaries via ACL
        List<MachinerySummaryRecord> allMachineries = this.externalMachineryService.getAllMachineriesForProjection();
        Map<String, MachinerySummaryRecord> machineryMap = (allMachineries != null) ? allMachineries.stream()
                .collect(Collectors.toMap(MachinerySummaryRecord::code, Function.identity(), (m1, m2) -> m1))
                : Map.of();

        List<AssignmentDetail> details = new ArrayList<>();

        for (Assignment a : assignments) {
            String operatorName = operatorNameMap.getOrDefault(a.getOperatorId(), "Desconocido");
            MachinerySummaryRecord machinery = machineryMap.get(a.getMachineryCode());
            Shift shift = shiftMap.get(a.getShiftId());

            // Filter by operatorName (ILIKE / case-insensitive contains)
            if (query.operatorName() != null && !query.operatorName().isBlank()) {
                String term = query.operatorName().trim().toLowerCase();
                if (!operatorName.toLowerCase().contains(term)) {
                    continue;
                }
            }

            // Filter by machineryCode
            if (query.machineryCode() != null && !query.machineryCode().isBlank()) {
                String term = query.machineryCode().trim().toLowerCase();
                if (!a.getMachineryCode().toLowerCase().contains(term)) {
                    continue;
                }
            }

            // Filter by machineryType (by ID or by name)
            if (query.machineryType() != null && !query.machineryType().isBlank()) {
                String term = query.machineryType().trim().toLowerCase();
                boolean matches = machinery != null && (
                        (machinery.machineryTypeName() != null && machinery.machineryTypeName().toLowerCase().contains(term)) ||
                        (machinery.machineryTypeId() != null && machinery.machineryTypeId().toString().equals(term)));
                if (!matches) {
                    continue;
                }
            }

            // Filter by shift startDate
            if (query.startDate() != null) {
                if (shift == null || shift.getDate() == null || shift.getDate().isBefore(query.startDate())) {
                    continue;
                }
            }

            // Filter by shift endDate
            if (query.endDate() != null) {
                if (shift == null || shift.getDate() == null || shift.getDate().isAfter(query.endDate())) {
                    continue;
                }
            }

            // Filter by shiftType (Boolean: true = Dia, false = Noche)
            if (query.shiftType() != null) {
                if (shift == null || !query.shiftType().equals(shift.getShiftType())) {
                    continue;
                }
            }

            Integer machineryTypeId = (machinery != null) ? machinery.machineryTypeId() : null;
            String machineryTypeName = (machinery != null && machinery.machineryTypeName() != null)
                    ? machinery.machineryTypeName() : "Desconocido";

            LocalDate shiftDate = (shift != null) ? shift.getDate() : null;
            Boolean shiftType = (shift != null) ? shift.getShiftType() : null;
            Integer shiftDuration = (shift != null) ? shift.getDuration() : null;

            details.add(new AssignmentDetail(
                    a.getId(),
                    a.getOperatorId(),
                    operatorName,
                    a.getMachineryCode(),
                    machineryTypeId,
                    machineryTypeName,
                    a.getShiftId(),
                    shiftDate,
                    shiftType,
                    shiftDuration,
                    a.getTimeStart(),
                    a.getTimeEnd(),
                    a.getActualShiftTime()
            ));
        }

        return details;
    }
}
