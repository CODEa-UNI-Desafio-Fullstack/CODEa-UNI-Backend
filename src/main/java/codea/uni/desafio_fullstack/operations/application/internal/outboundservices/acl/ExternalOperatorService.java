package codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl;

import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for the Operations Bounded Context to communicate with Operator Context.
 * Ensures the operations domain does not directly depend on Operator aggregates or repositories.
 */
public interface ExternalOperatorService {
    boolean existsOperatorById(UUID operatorId);
    boolean isOperatorCertifiedForMachineryType(UUID operatorId, Integer machineryTypeId, LocalDate shiftDate);
    List<OperatorSummaryRecord> getAllOperators();
    Optional<OperatorSummaryRecord> getOperatorSummary(UUID operatorId);
}
