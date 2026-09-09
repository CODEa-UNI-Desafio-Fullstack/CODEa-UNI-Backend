package codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;

import java.util.List;

/**
 * Outbound port for the Maintenance Bounded Context to communicate with Machinery Context.
 * Ensures the maintenance domain does not directly depend on Machinery aggregates or repositories.
 */
public interface ExternalMachineryService {
    boolean existsMachineryByCode(String machineryCode);
    void resetMachineryAfterMaintenance(String machineryCode);
    List<MachinerySummaryRecord> getAllMachineries();
}
