package codea.uni.desafio_fullstack.operators.application.internal.queryservices;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllCertificationsByOperatorIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MachineryCertificationQueryServiceImplTest {

    @Mock
    private MachineryCertificationRepository machineryCertificationRepository;

    @Mock
    private OperatorRepository operatorRepository;

    @InjectMocks
    private MachineryCertificationQueryServiceImpl queryService;

    private Operator operator;
    private UUID operatorId;

    @BeforeEach
    void setUp() {
        operatorId = UUID.randomUUID();
        operator = new Operator("Esteban Quito");
        operator.setId(operatorId);
    }

    @Test
    @DisplayName("handle(GetAllCertificationsByOperatorIdQuery) should return all certifications for existing operator")
    void handle_GetAllCertifications_WhenOperatorExists_ReturnsList() {
        var cert1 = new MachineryCertification(operator, 1, LocalDate.of(2027, 1, 1));
        var cert2 = new MachineryCertification(operator, 2, LocalDate.of(2027, 6, 1));

        when(operatorRepository.existsById(operatorId)).thenReturn(true);
        when(machineryCertificationRepository.findAllByIdOperatorId(operatorId)).thenReturn(List.of(cert1, cert2));

        var result = queryService.handle(new GetAllCertificationsByOperatorIdQuery(operatorId));

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(operatorRepository).existsById(operatorId);
        verify(machineryCertificationRepository).findAllByIdOperatorId(operatorId);
    }

    @Test
    @DisplayName("handle(GetAllCertificationsByOperatorIdQuery) should throw when operator does not exist")
    void handle_GetAllCertifications_WhenOperatorNotFound_Throws() {
        when(operatorRepository.existsById(operatorId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                queryService.handle(new GetAllCertificationsByOperatorIdQuery(operatorId)));
        verify(machineryCertificationRepository, never()).findAllByIdOperatorId(any());
    }

    @Test
    @DisplayName("handle(GetCertificationByOperatorIdAndMachineryTypeIdQuery) should return certification")
    void handle_GetCertificationById_ReturnsOptional() {
        var cert = new MachineryCertification(operator, 1, LocalDate.of(2027, 1, 1));

        when(machineryCertificationRepository.findByIdOperatorIdAndIdMachineryType(operatorId, 1))
                .thenReturn(Optional.of(cert));

        var result = queryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 1));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId().getMachineryType());
    }
}
