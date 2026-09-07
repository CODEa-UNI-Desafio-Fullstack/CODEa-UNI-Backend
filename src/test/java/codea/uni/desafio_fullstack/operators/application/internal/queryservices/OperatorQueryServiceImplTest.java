package codea.uni.desafio_fullstack.operators.application.internal.queryservices;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllOperatorsQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorQueryServiceImplTest {

    @Mock
    private OperatorRepository operatorRepository;

    @InjectMocks
    private OperatorQueryServiceImpl operatorQueryService;

    @Test
    @DisplayName("handle(GetOperatorByIdQuery) should return operator when found")
    void handle_GetOperatorById_ShouldReturnOperator() {
        var id = UUID.randomUUID();
        var operator = new Operator("Luis Diaz");
        operator.setId(id);

        when(operatorRepository.findById(id)).thenReturn(Optional.of(operator));

        var result = operatorQueryService.handle(new GetOperatorByIdQuery(id));

        assertTrue(result.isPresent());
        assertEquals("Luis Diaz", result.get().getName());
        assertEquals(id, result.get().getId());
        verify(operatorRepository).findById(id);
    }

    @Test
    @DisplayName("handle(GetAllOperatorsQuery) should return all operators")
    void handle_GetAllOperators_ShouldReturnList() {
        var op1 = new Operator("Luis Diaz");
        var op2 = new Operator("James Rodriguez");

        when(operatorRepository.findAll()).thenReturn(List.of(op1, op2));

        var result = operatorQueryService.handle(new GetAllOperatorsQuery());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(operatorRepository).findAll();
    }

    @Test
    @DisplayName("handle(GetOperatorsByMachineryTypeCertificationQuery) should return operators certified for machinery type")
    void handle_GetOperatorsByMachineryTypeCertification_ShouldReturnList() {
        var op1 = new Operator("Luis Diaz");
        when(operatorRepository.findDistinctByCertificationsIdMachineryType(1)).thenReturn(List.of(op1));

        var query = new codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByMachineryTypeCertificationQuery(1);
        var result = operatorQueryService.handle(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Luis Diaz", result.get(0).getName());
        verify(operatorRepository).findDistinctByCertificationsIdMachineryType(1);
    }

    @Test
    @DisplayName("handle(GetOperatorsByFilterQuery) without filters should return all operators")
    void handle_GetOperatorsByFilter_WhenNoFilter_ShouldReturnAll() {
        var op1 = new Operator("Luis Diaz");
        var op2 = new Operator("James Rodriguez");
        when(operatorRepository.findAll()).thenReturn(List.of(op1, op2));

        var query = new codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery(null, null);
        var result = operatorQueryService.handle(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(operatorRepository).findAll();
    }

    @Test
    @DisplayName("handle(GetOperatorsByFilterQuery) with machineryTypeId should return certified operators")
    void handle_GetOperatorsByFilter_WhenMachineryTypeId_ShouldReturnCertified() {
        var op1 = new Operator("Luis Diaz");
        when(operatorRepository.findDistinctByCertificationsIdMachineryType(2)).thenReturn(List.of(op1));

        var query = new codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery(2, null);
        var result = operatorQueryService.handle(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Luis Diaz", result.get(0).getName());
        verify(operatorRepository).findDistinctByCertificationsIdMachineryType(2);
    }

    @Test
    @DisplayName("handle(GetOperatorsByFilterQuery) with name filter should return matching operators")
    void handle_GetOperatorsByFilter_WhenName_ShouldReturnMatching() {
        var op1 = new Operator("Luis Diaz");
        var op2 = new Operator("James Rodriguez");
        when(operatorRepository.findAll()).thenReturn(List.of(op1, op2));

        var query = new codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery(null, "luis");
        var result = operatorQueryService.handle(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Luis Diaz", result.get(0).getName());
        verify(operatorRepository).findAll();
    }

    @Test
    @DisplayName("handle(GetOperatorsByFilterQuery) with both filters should return matching certified operators")
    void handle_GetOperatorsByFilter_WhenBothFilters_ShouldFilterByName() {
        var op1 = new Operator("Luis Diaz");
        var op2 = new Operator("Carlos Diaz");
        when(operatorRepository.findDistinctByCertificationsIdMachineryType(1)).thenReturn(List.of(op1, op2));

        var query = new codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery(1, "Carlos");
        var result = operatorQueryService.handle(query);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Carlos Diaz", result.get(0).getName());
        verify(operatorRepository).findDistinctByCertificationsIdMachineryType(1);
    }
}
