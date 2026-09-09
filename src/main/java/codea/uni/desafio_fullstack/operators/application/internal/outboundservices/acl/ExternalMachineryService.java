package codea.uni.desafio_fullstack.operators.application.internal.outboundservices.acl;

/**
 * Outbound port for the Operators Bounded Context to communicate with Machinery Context.
 * Ensures operators domain does not directly depend on Machinery aggregates or repositories.
 */
public interface ExternalMachineryService {
    boolean existsMachineryTypeById(Integer machineryTypeId);
}
