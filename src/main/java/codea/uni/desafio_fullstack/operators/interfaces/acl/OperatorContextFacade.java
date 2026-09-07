package codea.uni.desafio_fullstack.operators.interfaces.acl;

import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorCertificationSummaryRecord;
import codea.uni.desafio_fullstack.operators.interfaces.acl.records.OperatorSummaryRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Anti-Corruption Layer (ACL) Facade for the Operators Bounded Context.
 * Enables external contexts (operations, maintenance) to interact with operator
 * data and certification validity without coupling to internal models or repositories.
 */
public interface OperatorContextFacade {

    // Checks if an operator exists by ID.
    boolean existsById(UUID operatorId);

    // Retrieves an immutable summary of an operator by ID.
    Optional<OperatorSummaryRecord> getOperatorSummary(UUID operatorId);

    // Checks if an operator has a valid certification for a given machinery type on a specific shift date.
    boolean isOperatorCertifiedForMachineryType(UUID operatorId, Integer machineryTypeId, LocalDate shiftDate);

    // Retrieves a certification summary for an operator and machinery type.
    Optional<OperatorCertificationSummaryRecord> getCertificationSummary(UUID operatorId, Integer machineryTypeId);

    // Retrieves all operator IDs certified for a specific machinery type on a given shift date.
    List<UUID> getCertifiedOperatorIdsForMachineryType(Integer machineryTypeId, LocalDate shiftDate);

    // Retrieves all operators formatted as summaries.
    List<OperatorSummaryRecord> getAllOperators();
}
