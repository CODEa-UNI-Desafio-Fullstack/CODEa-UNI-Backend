package codea.uni.desafio_fullstack.operations.application.internal.outboundservices.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port for the Operations Bounded Context to communicate with Machinery Context.
 * Ensures the operations domain does not directly depend on Machinery aggregates or repositories.
 */
public interface ExternalMachineryService {
    boolean existsMachineryByCode(String machineryCode);
    boolean isMachineryActive(String machineryCode);
    Integer getMachineryTypeId(String machineryCode);
    Object recordWorkedHours (String machineryCode, float workedHours);
    List<MachinerySummaryRecord> getAllMachineriesForProjection();
    Optional<MachinerySummaryRecord> getMachinerySummary(String machineryCode);
}

