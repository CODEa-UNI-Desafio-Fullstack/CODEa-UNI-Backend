package codea.uni.desafio_fullstack.operators.interfaces.acl;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByMachineryTypeCertificationQuery;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationQueryService;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorQueryService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatorContextFacadeTest {

    @Mock
    private OperatorQueryService operatorQueryService;

    @Mock
    private MachineryCertificationQueryService machineryCertificationQueryService;

    @InjectMocks
    private OperatorContextFacadeImpl facade;

    private UUID operatorId;
    private Operator operator;

    @BeforeEach
    void setUp() {
        operatorId = UUID.randomUUID();
        operator = new Operator("Alejandro Toledo");
        operator.setId(operatorId);
    }

    @Test
    @DisplayName("existsById should return true when operator exists")
    void existsById_WhenExists_ReturnsTrue() {
        when(operatorQueryService.handle(any(GetOperatorByIdQuery.class))).thenReturn(Optional.of(operator));

        assertTrue(facade.existsById(operatorId));
        assertFalse(facade.existsById(null));
    }

    @Test
    @DisplayName("getOperatorSummary should return summary record when operator exists")
    void getOperatorSummary_WhenExists_ReturnsSummary() {
        when(operatorQueryService.handle(any(GetOperatorByIdQuery.class))).thenReturn(Optional.of(operator));

        var summary = facade.getOperatorSummary(operatorId);

        assertTrue(summary.isPresent());
        assertEquals(operatorId, summary.get().id());
        assertEquals("Alejandro Toledo", summary.get().name());
    }

    @Test
    @DisplayName("isOperatorCertifiedForMachineryType should evaluate validity adhering to Policy P5")
    void isOperatorCertified() {
        var shiftDate = LocalDate.of(2026, 9, 20);

        // Case 1: Certification expires strictly AFTER shift date -> VALID
        var validCert = new MachineryCertification(operator, 1, LocalDate.of(2026, 9, 21));
        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 1)))
                .thenReturn(Optional.of(validCert));
        assertTrue(facade.isOperatorCertifiedForMachineryType(operatorId, 1, shiftDate));

        // Case 2: Certification expires ON shift date -> INVALID (Policy P5)
        var expiresSameDayCert = new MachineryCertification(operator, 1, LocalDate.of(2026, 9, 20));
        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 1)))
                .thenReturn(Optional.of(expiresSameDayCert));
        assertFalse(facade.isOperatorCertifiedForMachineryType(operatorId, 1, shiftDate));

        // Case 3: Certification expired BEFORE shift date -> INVALID
        var expiredCert = new MachineryCertification(operator, 1, LocalDate.of(2026, 9, 19));
        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 1)))
                .thenReturn(Optional.of(expiredCert));
        assertFalse(facade.isOperatorCertifiedForMachineryType(operatorId, 1, shiftDate));

        // Case 4: No certification found -> INVALID
        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 2)))
                .thenReturn(Optional.empty());
        assertFalse(facade.isOperatorCertifiedForMachineryType(operatorId, 2, shiftDate));
    }

    @Test
    @DisplayName("getCertifiedOperatorIdsForMachineryType should return only valid operator IDs")
    void getCertifiedOperatorIds_ReturnsOnlyValid() {
        var shiftDate = LocalDate.of(2026, 9, 20);

        var op2Id = UUID.randomUUID();
        var op2 = new Operator("Beatriz Paredes");
        op2.setId(op2Id);

        when(operatorQueryService.handle(any(GetOperatorsByMachineryTypeCertificationQuery.class)))
                .thenReturn(List.of(operator, op2));

        var validCert = new MachineryCertification(operator, 1, LocalDate.of(2026, 10, 1));
        var expiredCert = new MachineryCertification(op2, 1, LocalDate.of(2026, 9, 15));

        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(operatorId, 1)))
                .thenReturn(Optional.of(validCert));
        when(machineryCertificationQueryService.handle(new GetCertificationByOperatorIdAndMachineryTypeIdQuery(op2Id, 1)))
                .thenReturn(Optional.of(expiredCert));

        var certifiedIds = facade.getCertifiedOperatorIdsForMachineryType(1, shiftDate);

        assertEquals(1, certifiedIds.size());
        assertEquals(operatorId, certifiedIds.get(0));
    }

    @Test
    @DisplayName("getAllOperators should return all operator summary records")
    void getAllOperators_ReturnsAllOperatorSummaries() {
        when(operatorQueryService.handle(any(codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllOperatorsQuery.class)))
                .thenReturn(List.of(operator));

        var summaries = facade.getAllOperators();

        assertNotNull(summaries);
        assertEquals(1, summaries.size());
        assertEquals(operatorId, summaries.get(0).id());
        assertEquals("Alejandro Toledo", summaries.get(0).name());
    }
}
