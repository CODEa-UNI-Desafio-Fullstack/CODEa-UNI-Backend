package codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl;

import java.util.UUID;

/**
 * Outbound port for the Maintenance Bounded Context to communicate with Operators Context.
 * Ensures the maintenance domain does not directly depend on Operator aggregates or repositories.
 */
public interface ExternalOperatorService {
    boolean existsOperatorById(UUID operatorId);
}
