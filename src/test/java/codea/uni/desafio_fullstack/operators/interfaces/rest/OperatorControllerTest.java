package codea.uni.desafio_fullstack.operators.interfaces.rest;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateOperatorNameCommand;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorCommandService;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorQueryService;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.CreateOperatorResource;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.UpdateOperatorNameResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorControllerTest {

    @Mock
    private OperatorCommandService operatorCommandService;

    @Mock
    private OperatorQueryService operatorQueryService;

    @InjectMocks
    private OperatorController operatorController;

    private Operator operator;
    private UUID operatorId;

    @BeforeEach
    void setUp() {
        operatorId = UUID.randomUUID();
        operator = new Operator("Luis Diaz");
        operator.setId(operatorId);
    }

    @Test
    @DisplayName("getAllOperators should return list of resources with filters")
    void getAllOperators_ShouldReturnFilteredList() {
        when(operatorQueryService.handle(any(GetOperatorsByFilterQuery.class)))
                .thenReturn(List.of(operator));

        var response = operatorController.getAllOperators(1, "Luis");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Luis Diaz", response.getBody().get(0).name());
        assertEquals(operatorId, response.getBody().get(0).id());
    }

    @Test
    @DisplayName("getOperatorById should return 200 when found")
    void getOperatorById_WhenFound_ShouldReturnOk() {
        when(operatorQueryService.handle(any(GetOperatorByIdQuery.class)))
                .thenReturn(Optional.of(operator));

        var response = operatorController.getOperatorById(operatorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Luis Diaz", response.getBody().name());
    }

    @Test
    @DisplayName("getOperatorById should return 404 when not found")
    void getOperatorById_WhenNotFound_ShouldReturnNotFound() {
        when(operatorQueryService.handle(any(GetOperatorByIdQuery.class)))
                .thenReturn(Optional.empty());

        var response = operatorController.getOperatorById(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("registerOperator should return 201 when successful")
    void registerOperator_WhenSuccessful_ShouldReturnCreated() {
        when(operatorCommandService.handle(any(CreateOperatorCommand.class)))
                .thenReturn(Optional.of(operator));

        var resource = new CreateOperatorResource("Luis Diaz");
        var response = operatorController.registerOperator(resource);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Luis Diaz", response.getBody().name());
    }

    @Test
    @DisplayName("updateOperatorName should return 200 when successful")
    void updateOperatorName_WhenSuccessful_ShouldReturnOk() {
        when(operatorCommandService.handle(any(UpdateOperatorNameCommand.class)))
                .thenReturn(Optional.of(operator));

        var resource = new UpdateOperatorNameResource("Luis Fernando Diaz");
        var response = operatorController.updateOperatorName(operatorId, resource);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
