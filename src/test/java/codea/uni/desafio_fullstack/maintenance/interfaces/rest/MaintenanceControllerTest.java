package codea.uni.desafio_fullstack.maintenance.interfaces.rest;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenanceByIdQuery;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenancesByFilterQuery;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceCommandService;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceControllerTest {

    @Mock
    private MaintenanceCommandService maintenanceCommandService;

    @Mock
    private MaintenanceQueryService maintenanceQueryService;

    @InjectMocks
    private MaintenanceController maintenanceController;

    private Maintenance createSampleMaintenance(String machineryCode, LocalDate date, UUID operatorId) {
        CreateMaintenanceCommand command = new CreateMaintenanceCommand(
                machineryCode,
                date,
                150.0f,
                operatorId,
                "Routine checkup"
        );
        return new Maintenance(command);
    }

    @Test
    @DisplayName("getAllMaintenances should return list of resources when filtered")
    void getAllMaintenances_ShouldReturnFilteredList() {
        UUID operatorId = UUID.randomUUID();
        Maintenance m = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 10), operatorId);
        m.setId(UUID.randomUUID());

        when(maintenanceQueryService.handle(any(GetMaintenancesByFilterQuery.class)))
                .thenReturn(List.of(m));

        var response = maintenanceController.getAllMaintenances(operatorId, "CAM-001", 1,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("CAM-001", response.getBody().get(0).machineryCode());
        assertEquals(operatorId, response.getBody().get(0).operatorId());
        assertEquals(150.0f, response.getBody().get(0).hourMeter());
    }

    @Test
    @DisplayName("getMaintenanceById should return 200 when found")
    void getMaintenanceById_WhenFound_ShouldReturnOk() {
        UUID maintenanceId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        Maintenance m = createSampleMaintenance("CAM-001", LocalDate.of(2026, 9, 10), operatorId);
        m.setId(maintenanceId);

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class)))
                .thenReturn(Optional.of(m));

        var response = maintenanceController.getMaintenanceById(maintenanceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(maintenanceId, response.getBody().id());
    }

    @Test
    @DisplayName("getMaintenanceById should return 404 when not found")
    void getMaintenanceById_WhenNotFound_ShouldReturnNotFound() {
        UUID maintenanceId = UUID.randomUUID();

        when(maintenanceQueryService.handle(any(GetMaintenanceByIdQuery.class)))
                .thenReturn(Optional.empty());

        var response = maintenanceController.getMaintenanceById(maintenanceId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
