package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetMachineryMaintenanceProjectionsQuery;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentQueryServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    @Mock
    private ExternalOperatorService externalOperatorService;

    private AssignmentQueryServiceImpl assignmentQueryService;

    @BeforeEach
    void setUp() {
        assignmentQueryService = new AssignmentQueryServiceImpl(
                assignmentRepository,
                shiftRepository,
                externalMachineryService,
                externalOperatorService
        );
    }

    @Test
    @DisplayName("Should get assignment by ID when exists")
    void shouldGetAssignmentByIdWhenExists() {
        UUID assignmentId = UUID.randomUUID();
        Assignment assignment = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EQ-001", UUID.randomUUID()));

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        Optional<Assignment> result = assignmentQueryService.handle(new GetAssignmentByIdQuery(assignmentId));

        assertTrue(result.isPresent());
        assertEquals("EQ-001", result.get().getMachineryCode());
        verify(assignmentRepository, times(1)).findById(assignmentId);
    }

    @Test
    @DisplayName("Should return empty when assignment does not exist")
    void shouldReturnEmptyWhenAssignmentDoesNotExist() {
        UUID assignmentId = UUID.randomUUID();
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        Optional<Assignment> result = assignmentQueryService.handle(new GetAssignmentByIdQuery(assignmentId));

        assertFalse(result.isPresent());
        verify(assignmentRepository, times(1)).findById(assignmentId);
    }

    @Test
    @DisplayName("Should project 7-day maintenance for active machineries that will be blocked, ignoring already blocked ones")
    void shouldProject7DayMaintenanceForActiveMachineriesThatWillBeBlocked() {
        // Active machinery CAM-001: threshold 500, current 400, remaining 100
        var cam001 = new MachinerySummaryRecord("CAM-001", 1, "Camión de Acarreo", 400.0f, 500, true, false, 100.0f);
        // Active machinery EXC-002: threshold 500, current 100, remaining 400
        var exc002 = new MachinerySummaryRecord("EXC-002", 2, "Excavadora", 100.0f, 500, true, false, 400.0f);
        // Already blocked machinery PER-003: should be ignored!
        var per003 = new MachinerySummaryRecord("PER-003", 3, "Perforadora", 510.0f, 500, false, true, 0.0f);

        when(externalMachineryService.getAllMachineriesForProjection()).thenReturn(List.of(cam001, exc002, per003));

        // Create future shifts within 7 days
        UUID shift1Id = UUID.randomUUID();
        Shift shift1 = new Shift(new CreateShiftCommand(LocalDate.now().plusDays(1), true, 60));
        shift1.setId(shift1Id);

        UUID shift2Id = UUID.randomUUID();
        Shift shift2 = new Shift(new CreateShiftCommand(LocalDate.now().plusDays(2), false, 50));
        shift2.setId(shift2Id);

        UUID shift3Id = UUID.randomUUID();
        Shift shift3 = new Shift(new CreateShiftCommand(LocalDate.now().plusDays(3), true, 10));
        shift3.setId(shift3Id);

        when(shiftRepository.findShiftsBetweenDatesSorted(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(shift1, shift2, shift3));

        // Assignments:
        // CAM-001 is assigned to shift1 (60h) and shift2 (50h) -> total projected 110h >= 100h remaining
        Assignment assignCam1 = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "CAM-001", shift1Id));
        Assignment assignCam2 = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "CAM-001", shift2Id));
        // EXC-002 is assigned to shift3 (10h) -> projected 10h < 400h remaining
        Assignment assignExc = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "EXC-002", shift3Id));

        when(assignmentRepository.findAllByShiftIdIn(List.of(shift1Id, shift2Id, shift3Id)))
                .thenReturn(List.of(assignCam1, assignCam2, assignExc));

        var query = new GetMachineryMaintenanceProjectionsQuery(null, null);
        var projections = assignmentQueryService.handle(query);

        assertEquals(1, projections.size());
        var camProj = projections.get(0);
        assertEquals("CAM-001", camProj.machineryCode());
        assertEquals("Camión de Acarreo", camProj.machineryTypeName());
        assertEquals(100.0f, camProj.remainingHours());
        assertEquals(110.0f, camProj.projectedHours());
        assertEquals(10.0f, camProj.differenceHours());
        // Threshold reached on shift 2!
        assertEquals(shift2.getDate(), camProj.estimatedThresholdDate());
        assertEquals(shift2Id, camProj.estimatedThresholdShiftId());
        assertEquals("Noche", camProj.estimatedThresholdShiftType());
    }

    @Test
    @DisplayName("Should filter projections by machineryCode and machineryType")
    void shouldFilterProjectionsByMachineryCodeAndType() {
        var cam001 = new MachinerySummaryRecord("CAM-001", 1, "Camión de Acarreo", 400.0f, 500, true, false, 50.0f);
        var cam002 = new MachinerySummaryRecord("CAM-002", 1, "Camión de Acarreo", 450.0f, 500, true, false, 50.0f);

        when(externalMachineryService.getAllMachineriesForProjection()).thenReturn(List.of(cam001, cam002));

        UUID shiftId = UUID.randomUUID();
        Shift shift = new Shift(new CreateShiftCommand(LocalDate.now().plusDays(1), true, 60));
        shift.setId(shiftId);

        when(shiftRepository.findShiftsBetweenDatesSorted(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(shift));

        Assignment assignCam1 = new Assignment(new CreateAssignmentCommand(UUID.randomUUID(), "CAM-001", shiftId));
        when(assignmentRepository.findAllByShiftIdIn(List.of(shiftId)))
                .thenReturn(List.of(assignCam1));

        // Filter by specific code "CAM-001"
        var query = new GetMachineryMaintenanceProjectionsQuery("CAM-001", "Camión");
        var projections = assignmentQueryService.handle(query);

        assertEquals(1, projections.size());
        assertEquals("CAM-001", projections.get(0).machineryCode());

        // Filter by non-existent code
        var emptyQuery = new GetMachineryMaintenanceProjectionsQuery("NON-EXISTENT", null);
        var emptyResult = assignmentQueryService.handle(emptyQuery);
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    @DisplayName("Should return all assignments enriched with details and handle filters")
    void shouldReturnAllAssignmentsEnrichedAndHandleFilters() {
        UUID op1Id = UUID.randomUUID();
        UUID op2Id = UUID.randomUUID();
        when(externalOperatorService.getAllOperators()).thenReturn(List.of(
                new OperatorSummaryRecord(op1Id, "Carlos Mendoza"),
                new OperatorSummaryRecord(op2Id, "Beatriz Paredes")
        ));

        when(externalMachineryService.getAllMachineriesForProjection()).thenReturn(List.of(
                new MachinerySummaryRecord("CAM-001", 1, "Camión de Acarreo", 200.0f, 500, true, false, 300.0f),
                new MachinerySummaryRecord("EXC-001", 2, "Excavadora", 150.0f, 500, true, false, 350.0f)
        ));

        UUID shift1Id = UUID.randomUUID();
        Shift shift1 = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 10), true, 12));
        shift1.setId(shift1Id);

        UUID shift2Id = UUID.randomUUID();
        Shift shift2 = new Shift(new CreateShiftCommand(LocalDate.of(2026, 9, 15), false, 8));
        shift2.setId(shift2Id);

        when(shiftRepository.findAllById(any())).thenReturn(List.of(shift1, shift2));

        Assignment a1 = new Assignment(new CreateAssignmentCommand(op1Id, "CAM-001", shift1Id));
        a1.setId(UUID.randomUUID());
        Assignment a2 = new Assignment(new CreateAssignmentCommand(op2Id, "EXC-001", shift2Id));
        a2.setId(UUID.randomUUID());

        when(assignmentRepository.findAll()).thenReturn(List.of(a1, a2));

        // 1. Query with no filters: should return both enriched assignments
        var allQuery = new GetAssignmentsByFilterQuery(null, null, null, null, null, null);
        var allResults = assignmentQueryService.handle(allQuery);
        assertEquals(2, allResults.size());
        assertEquals("Carlos Mendoza", allResults.get(0).operatorName());
        assertEquals("CAM-001", allResults.get(0).machineryCode());
        assertEquals("Camión de Acarreo", allResults.get(0).machineryTypeName());
        assertEquals(Boolean.TRUE, allResults.get(0).shiftType());
        assertEquals(12, allResults.get(0).shiftDuration());

        // 2. Query filter by operatorName (ILIKE / contains)
        var opFilterQuery = new GetAssignmentsByFilterQuery("carlos", null, null, null, null, null);
        var opResults = assignmentQueryService.handle(opFilterQuery);
        assertEquals(1, opResults.size());
        assertEquals("Carlos Mendoza", opResults.get(0).operatorName());

        // 3. Query filter by machineryType
        var typeFilterQuery = new GetAssignmentsByFilterQuery(null, "Excavadora", null, null, null, null);
        var typeResults = assignmentQueryService.handle(typeFilterQuery);
        assertEquals(1, typeResults.size());
        assertEquals("EXC-001", typeResults.get(0).machineryCode());

        // 4. Query filter by date range
        var dateFilterQuery = new GetAssignmentsByFilterQuery(null, null, null,
                LocalDate.of(2026, 9, 12), LocalDate.of(2026, 9, 20), null);
        var dateResults = assignmentQueryService.handle(dateFilterQuery);
        assertEquals(1, dateResults.size());
        assertEquals("EXC-001", dateResults.get(0).machineryCode());

        // 5. Query filter by shiftType (Boolean.TRUE for Dia)
        var shiftTypeQuery = new GetAssignmentsByFilterQuery(null, null, null, null, null, Boolean.TRUE);
        var shiftTypeResults = assignmentQueryService.handle(shiftTypeQuery);
        assertEquals(1, shiftTypeResults.size());
        assertEquals("Carlos Mendoza", shiftTypeResults.get(0).operatorName());
        assertEquals(Boolean.TRUE, shiftTypeResults.get(0).shiftType());
    }
}
