package codea.uni.desafio_fullstack.maintenance.interfaces.acl;

import codea.uni.desafio_fullstack.maintenance.interfaces.acl.records.MaintenanceSummaryRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceContextFacade {
    boolean existsById(UUID id);
    Optional<MaintenanceSummaryRecord> getMaintenanceSummary(UUID id);
    List<MaintenanceSummaryRecord> getMaintenanceSummariesByMachineryCode(String machineryCode);
}
