package codea.uni.desafio_fullstack.operations.interfaces.rest;

import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetMachineryMaintenanceProjectionsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.AssignmentDetail;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.MachineryMaintenanceProjection;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentCommandService;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentQueryService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentControllerTest {

    @Mock
    private AssignmentCommandService assignmentCommandService;

    @Mock
    private AssignmentQueryService assignmentQueryService;

    @InjectMocks
    private AssignmentController assignmentController;

    @Test
    @DisplayName("getMachineryMaintenanceProjections should return list of projections")
    void getMachineryMaintenanceProjections_ShouldReturnOk() {
        var projection = new MachineryMaintenanceProjection(
                "CAM-001",
                1,
                "Camión de Acarreo",
                400.0f,
                500,
                100.0f,
                120.0f,
                20.0f,
                LocalDate.now().plusDays(2),
                UUID.randomUUID(),
                "Noche"
        );

        when(assignmentQueryService.handle(any(GetMachineryMaintenanceProjectionsQuery.class)))
                .thenReturn(List.of(projection));

        var response = assignmentController.getMachineryMaintenanceProjections("CAM-001", "Camión");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("CAM-001", response.getBody().get(0).machineryCode());
        assertEquals(120.0f, response.getBody().get(0).projectedHours());
        assertEquals(20.0f, response.getBody().get(0).differenceHours());
    }

    @Test
    @DisplayName("getAllAssignments should return list of enriched assignment details")
    void getAllAssignments_ShouldReturnOk() {
        UUID assignmentId = UUID.randomUUID();
        UUID operatorId = UUID.randomUUID();
        UUID shiftId = UUID.randomUUID();

        var detail = new AssignmentDetail(
                assignmentId,
                operatorId,
                "Carlos Mendoza",
                "CAM-001",
                1,
                "Camión de Acarreo",
                shiftId,
                LocalDate.now(),
                Boolean.TRUE,
                12,
                null,
                null,
                null
        );

        when(assignmentQueryService.handle(any(GetAssignmentsByFilterQuery.class)))
                .thenReturn(List.of(detail));

        var response = assignmentController.getAllAssignments("Carlos", "Camión", "CAM-001", null, null, Boolean.TRUE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Carlos Mendoza", response.getBody().get(0).operatorName());
        assertEquals("CAM-001", response.getBody().get(0).machineryCode());
        assertEquals(Boolean.TRUE, response.getBody().get(0).shiftType());
    }
}
