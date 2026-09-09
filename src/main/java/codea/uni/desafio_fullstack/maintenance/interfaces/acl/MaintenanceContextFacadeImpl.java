package codea.uni.desafio_fullstack.maintenance.interfaces.acl;

import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenanceByIdQuery;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.GetMaintenancesByMachineryCodeQuery;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceQueryService;
import codea.uni.desafio_fullstack.maintenance.interfaces.acl.records.MaintenanceSummaryRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MaintenanceContextFacadeImpl implements MaintenanceContextFacade {

    private final MaintenanceQueryService maintenanceQueryService;

    public MaintenanceContextFacadeImpl(MaintenanceQueryService maintenanceQueryService) {
        this.maintenanceQueryService = maintenanceQueryService;
    }

    @Override
    public boolean existsById(UUID id) {
        return this.maintenanceQueryService.handle(new GetMaintenanceByIdQuery(id)).isPresent();
    }

    @Override
    public Optional<MaintenanceSummaryRecord> getMaintenanceSummary(UUID id) {
        return this.maintenanceQueryService.handle(new GetMaintenanceByIdQuery(id))
                .map(m -> new MaintenanceSummaryRecord(
                        m.getId(),
                        m.getMachineryCode(),
                        m.getDate(),
                        m.getHourMeter(),
                        m.getOperatorId(),
                        m.getObservation()
                ));
    }

    @Override
    public List<MaintenanceSummaryRecord> getMaintenanceSummariesByMachineryCode(String machineryCode) {
        return this.maintenanceQueryService.handle(new GetMaintenancesByMachineryCodeQuery(machineryCode))
                .stream()
                .map(m -> new MaintenanceSummaryRecord(
                        m.getId(),
                        m.getMachineryCode(),
                        m.getDate(),
                        m.getHourMeter(),
                        m.getOperatorId(),
                        m.getObservation()
                ))
                .toList();
    }
}
