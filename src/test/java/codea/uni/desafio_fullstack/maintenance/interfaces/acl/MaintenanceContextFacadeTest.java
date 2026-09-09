package codea.uni.desafio_fullstack.maintenance.interfaces.acl;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenanceByIdQuery;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenancesByMachineryCodeQuery;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceQueryService;
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
class MaintenanceContextFacadeTest {

    @Mock
    private MaintenanceQueryService maintenanceQueryService;

    private MaintenanceContextFacadeImpl maintenanceContextFacade;

    @BeforeEach
    void setUp() {
        maintenanceContextFacade = new MaintenanceContextFacadeImpl(maintenanceQueryService);
    }

    private Maintenance createSampleMaintenance(String machineryCode, LocalDate date, UUID operatorId) {
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                machineryCode,
                date,
                200.0f,
                operatorId,
                "Test observation"
        );
        return new Maintenance(command);
    }

    @Test
    @DisplayName("Should return true when maintenance exists by ID")
    void shouldReturnTrueWhenMaintenanceExistsById() {
        UUID id = UUID.randomUUID();
        Maintenance maintenance = createSampleMaintenance("EQ-001", LocalDate.now(), UUID.randomUUID());

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class))).thenReturn(Optional.of(maintenance));

        boolean exists = maintenanceContextFacade.existsById(id);

        assertTrue(exists);
        verify(maintenanceQueryService, times(1)).handle(any(GetMaintenanceByIdQuery.class));
    }

    @Test
    @DisplayName("Should return false when maintenance does not exist by ID")
    void shouldReturnFalseWhenMaintenanceDoesNotExistById() {
        UUID id = UUID.randomUUID();

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class))).thenReturn(Optional.empty());

        boolean exists = maintenanceContextFacade.existsById(id);

        assertFalse(exists);
        verify(maintenanceQueryService, times(1)).handle(any(GetMaintenanceByIdQuery.class));
    }

    @Test
    @DisplayName("Should return maintenance summary when exists")
    void shouldReturnMaintenanceSummaryWhenExists() {
        UUID id = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 6);
        Maintenance maintenance = createSampleMaintenance("EQ-001", date, operatorId);

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class))).thenReturn(Optional.of(maintenance));

        var summary = maintenanceContextFacade.getMaintenanceSummary(id);

        assertTrue(summary.isPresent());
        assertEquals("EQ-001", summary.get().machineryCode());
        assertEquals(200.0f, summary.get().hourMeter());
        assertEquals(operatorId, summary.get().operatorId());
        assertEquals(date, summary.get().date());
    }

    @Test
    @DisplayName("Should return empty summary when maintenance does not exist")
    void shouldReturnEmptySummaryWhenDoesNotExist() {
        UUID id = UUID.randomUUID();

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class))).thenReturn(Optional.empty());

        var summary = maintenanceContextFacade.getMaintenanceSummary(id);

        assertTrue(summary.isEmpty());
    }

    @Test
    @DisplayName("Should return maintenance summaries by machinery code")
    void shouldReturnMaintenanceSummariesByMachineryCode() {
        UUID operatorId = UUID.randomUUID();
        LocalDate date = LocalDate.of(2026, 9, 6);
        Maintenance maintenance = createSampleMaintenance("EQ-001", date, operatorId);

        when(maintenanceQueryService.handle(any(GetMaintenancesByMachineryCodeQuery.class)))
                .thenReturn(List.of(maintenance));

        var summaries = maintenanceContextFacade.getMaintenanceSummariesByMachineryCode("EQ-001");

        assertEquals(1, summaries.size());
        assertEquals("EQ-001", summaries.get(0).machineryCode());
        assertEquals(operatorId, summaries.get(0).operatorId());
    }
}
