package codea.uni.desafio_fullstack.maintenance.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.*;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceQueryServiceImplTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private ExternalMachineryService externalMachineryService;

    private MaintenanceQueryServiceImpl maintenanceQueryService;

    @BeforeEach
    void setUp() {
        maintenanceQueryService = new MaintenanceQueryServiceImpl(maintenanceRepository, externalMachineryService);
    }

    private Maintenance createSampleMaintenance(String machineryCode, LocalDate date, UUID operatorId) {
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                machineryCode,
                date,
                150.0f,
                operatorId,
                "Sample maintenance"
        );
        return new Maintenance(command);
    }

    @Test
    @DisplayName("Should get all maintenances")
    void shouldGetAllMaintenances() {
        UUID operatorId = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("EQ-001", LocalDate.of(2026, 9, 1), operatorId);
        Maintenance m2 = createSampleMaintenance("EQ-002", LocalDate.of(2026, 9, 2), operatorId);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2));

        var result = maintenanceQueryService.handle(new GetAllMaintenancesQuery());

        assertEquals(2, result.size());
        verify(maintenanceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get maintenance by ID when exists")
    void shouldGetMaintenanceByIdWhenExists() {
        UUID maintenanceId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        Maintenance m = createSampleMaintenance("EQ-001", LocalDate.of(2026, 9, 1), operatorId);

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.of(m));

        var result = maintenanceQueryService.handle(new GetMaintenanceByIdQuery(maintenanceId));

        assertTrue(result.isPresent());
        assertEquals("EQ-001", result.get().getMachineryCode());
        verify(maintenanceRepository, times(1)).findById(maintenanceId);
    }

    @Test
    @DisplayName("Should return empty optional when maintenance by ID does not exist")
    void shouldReturnEmptyWhenMaintenanceByIdDoesNotExist() {
        UUID maintenanceId = UUID.randomUUID();

        when(maintenanceRepository.findById(maintenanceId)).thenReturn(Optional.empty());

        var result = maintenanceQueryService.handle(new GetMaintenanceByIdQuery(maintenanceId));

        assertTrue(result.isEmpty());
        verify(maintenanceRepository, times(1)).findById(maintenanceId);
    }

    @Test
    @DisplayName("Should get maintenances by machinery code")
    void shouldGetMaintenancesByMachineryCode() {
        UUID operatorId = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("EQ-001", LocalDate.of(2026, 9, 1), operatorId);

        when(maintenanceRepository.findAllByMachineryCode("EQ-001")).thenReturn(List.of(m1));

        var result = maintenanceQueryService.handle(new GetMaintenancesByMachineryCodeQuery("EQ-001"));

        assertEquals(1, result.size());
        assertEquals("EQ-001", result.get(0).getMachineryCode());
        verify(maintenanceRepository, times(1)).findAllByMachineryCode("EQ-001");
    }

    @Test
    @DisplayName("Should get maintenances by operator ID")
    void shouldGetMaintenancesByOperatorId() {
        UUID operatorId = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("EQ-001", LocalDate.of(2026, 9, 1), operatorId);

        when(maintenanceRepository.findAllByOperatorId(operatorId)).thenReturn(List.of(m1));

        var result = maintenanceQueryService.handle(new GetMaintenancesByOperatorIdQuery(operatorId));

        assertEquals(1, result.size());
        assertEquals(operatorId, result.get(0).getOperatorId());
        verify(maintenanceRepository, times(1)).findAllByOperatorId(operatorId);
    }

    @Test
    @DisplayName("Should get maintenances by date range")
    void shouldGetMaintenancesByDateRange() {
        UUID operatorId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        LocalDate endDate = LocalDate.of(2026, 9, 30);
        Maintenance m1 = createSampleMaintenance("EQ-001", LocalDate.of(2026, 9, 15), operatorId);

        when(maintenanceRepository.findAllByDateBetween(startDate, endDate)).thenReturn(List.of(m1));

        var result = maintenanceQueryService.handle(new GetMaintenancesByDateRangeQuery(startDate, endDate));

        assertEquals(1, result.size());
        verify(maintenanceRepository, times(1)).findAllByDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should return all maintenances when filter query has no filters")
    void shouldReturnAllMaintenances_WhenNoFiltersProvided() {
        UUID op1 = UUID.randomUUID();
        UUID op2 = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 1), op1);
        Maintenance m2 = createSampleMaintenance("EXC-001", LocalDate.of(2026, 9, 5), op2);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2));

        var query = new GetMaintenancesByFilterQuery(null, null, null, null, null);
        var results = maintenanceQueryService.handle(query);

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Should filter maintenances by operatorId")
    void shouldFilterMaintenances_ByOperatorId() {
        UUID op1 = UUID.randomUUID();
        UUID op2 = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 1), op1);
        Maintenance m2 = createSampleMaintenance("EXC-001", LocalDate.of(2026, 9, 5), op2);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2));

        var query = new GetMaintenancesByFilterQuery(op1, null, null, null, null);
        var results = maintenanceQueryService.handle(query);

        assertEquals(1, results.size());
        assertEquals("CAM-001", results.get(0).getMachineryCode());
        assertEquals(op1, results.get(0).getOperatorId());
    }

    @Test
    @DisplayName("Should filter maintenances by machineryCode (case-insensitive substring)")
    void shouldFilterMaintenances_ByMachineryCode() {
        UUID op = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 1), op);
        Maintenance m2 = createSampleMaintenance("EXC-001", LocalDate.of(2026, 9, 5), op);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2));

        var query = new GetMaintenancesByFilterQuery(null, "cam", null, null, null);
        var results = maintenanceQueryService.handle(query);

        assertEquals(1, results.size());
        assertEquals("CAM-001", results.get(0).getMachineryCode());
    }

    @Test
    @DisplayName("Should filter maintenances by date range")
    void shouldFilterMaintenances_ByDateRange() {
        UUID op = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 2), op);
        Maintenance m2 = createSampleMaintenance("CAM-002", LocalDate.of(2026, 9, 15), op);
        Maintenance m3 = createSampleMaintenance("CAM-003", LocalDate.of(2026, 9, 28), op);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2, m3));

        var query = new GetMaintenancesByFilterQuery(null, null, null,
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 20));
        var results = maintenanceQueryService.handle(query);

        assertEquals(1, results.size());
        assertEquals("CAM-002", results.get(0).getMachineryCode());
    }

    @Test
    @DisplayName("Should filter maintenances by machineryTypeId using external machinery service")
    void shouldFilterMaintenances_ByMachineryTypeId() {
        UUID op = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 1), op);
        Maintenance m2 = createSampleMaintenance("EXC-001", LocalDate.of(2026, 9, 5), op);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2));
        when(externalMachineryService.getAllMachineries()).thenReturn(List.of(
                new MachinerySummaryRecord("CAM-001", 1, "Camión de Acarreo", 100.0f, 500, true, false, 400.0f),
                new MachinerySummaryRecord("EXC-001", 2, "Excavadora", 200.0f, 600, true, false, 400.0f)
        ));

        // Filter for type ID 2 (Excavadora)
        var query = new GetMaintenancesByFilterQuery(null, null, 2, null, null);
        var results = maintenanceQueryService.handle(query);

        assertEquals(1, results.size());
        assertEquals("EXC-001", results.get(0).getMachineryCode());
    }

    @Test
    @DisplayName("Should filter maintenances combining multiple criteria")
    void shouldFilterMaintenances_MultipleCriteriaCombined() {
        UUID op1 = UUID.randomUUID();
        UUID op2 = UUID.randomUUID();
        Maintenance m1 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 10), op1);
        Maintenance m2 = createSampleMaintenance("CAM-002", LocalDate.of(2026, 9, 10), op2);
        Maintenance m3 = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 25), op1);

        when(maintenanceRepository.findAll()).thenReturn(List.of(m1, m2, m3));
        when(externalMachineryService.getAllMachineries()).thenReturn(List.of(
                new MachinerySummaryRecord("CAM-001", 1, "Camión de Acarreo", 100.0f, 500, true, false, 400.0f),
                new MachinerySummaryRecord("CAM-002", 1, "Camión de Acarreo", 100.0f, 500, true, false, 400.0f)
        ));

        // Filter: op1 + "CAM" + typeId 1 + date between 2026-09-01 and 2026-09-15
        var query = new GetMaintenancesByFilterQuery(op1, "CAM", 1,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 15));
        var results = maintenanceQueryService.handle(query);

        assertEquals(1, results.size());
        assertEquals("CAM-001", results.get(0).getMachineryCode());
        assertEquals(LocalDate.of(2026, 9, 10), results.get(0).getDate());
    }
}
