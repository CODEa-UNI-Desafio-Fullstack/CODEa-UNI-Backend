package codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl;

/**
 * Outbound port for the Operations Bounded Context to communicate with Machinery Context.
 * Ensures the operations domain does not directly depend on Machinery aggregates or repositories.
 */
public interface ExternalMachineryService {
    boolean existsMachineryByCode(String machineryCode);
    boolean isMachineryActive(String machineryCode);
    Integer getMachineryTypeId(String machineryCode);
    Object recordWorkedHours (String machineryCode, float workedHours);
}

