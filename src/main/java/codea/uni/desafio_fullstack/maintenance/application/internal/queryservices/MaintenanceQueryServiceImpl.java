package codea.uni.desafio_fullstack.maintenance.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.interfaces.acl.records.MachinerySummaryRecord;
import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.*;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceQueryService;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MaintenanceQueryServiceImpl implements MaintenanceQueryService {

    private final MaintenanceRepository maintenanceRepository;
    private final ExternalMachineryService externalMachineryService;

    public MaintenanceQueryServiceImpl(MaintenanceRepository maintenanceRepository,
                                       ExternalMachineryService externalMachineryService) {
        this.maintenanceRepository = maintenanceRepository;
        this.externalMachineryService = externalMachineryService;
    }

    @Override
    public List<Maintenance> handle(GetAllMaintenancesQuery query) {
        return this.maintenanceRepository.findAll();
    }

    @Override
    public Optional<Maintenance> handle(GetMaintenanceByIdQuery query) {
        return this.maintenanceRepository.findById(query.id());
    }

    @Override
    public List<Maintenance> handle(GetMaintenancesByMachineryCodeQuery query) {
        return this.maintenanceRepository.findAllByMachineryCode(query.machineryCode().trim());
    }

    @Override
    public List<Maintenance> handle(GetMaintenancesByDateRangeQuery query) {
        return this.maintenanceRepository.findAllByDateBetween(query.startDate(), query.endDate());
    }

    @Override
    public List<Maintenance> handle(GetMaintenancesByOperatorIdQuery query) {
        return this.maintenanceRepository.findAllByOperatorId(query.operatorId());
    }

    @Override
    public List<Maintenance> handle(GetMaintenancesByFilterQuery query) {
        List<Maintenance> maintenances = this.maintenanceRepository.findAll();
        if (maintenances.isEmpty()) {
            return List.of();
        }

        Map<String, Integer> machineryTypeMap = null;
        if (query.machineryTypeId() != null) {
            var allMachineries = this.externalMachineryService.getAllMachineries();
            machineryTypeMap = (allMachineries != null)
                    ? allMachineries.stream()
                        .filter(m -> m.machineryTypeId() != null)
                        .collect(Collectors.toMap(MachinerySummaryRecord::code, MachinerySummaryRecord::machineryTypeId, (m1, m2) -> m1))
                    : Map.of();
        }

        final Map<String, Integer> finalMachineryTypeMap = machineryTypeMap;

        return maintenances.stream()
                .filter(m -> {
                    if (query.operatorId() != null && !query.operatorId().equals(m.getOperatorId())) {
                        return false;
                    }

                    if (query.machineryCode() != null && !query.machineryCode().isBlank()) {
                        String term = query.machineryCode().trim().toLowerCase();
                        if (m.getMachineryCode() == null || !m.getMachineryCode().toLowerCase().contains(term)) {
                            return false;
                        }
                    }

                    if (query.startDate() != null) {
                        if (m.getDate() == null || m.getDate().isBefore(query.startDate())) {
                            return false;
                        }
                    }

                    if (query.endDate() != null) {
                        if (m.getDate() == null || m.getDate().isAfter(query.endDate())) {
                            return false;
                        }
                    }

                    if (query.machineryTypeId() != null) {
                        if (finalMachineryTypeMap == null) {
                            return false;
                        }
                        Integer typeId = finalMachineryTypeMap.get(m.getMachineryCode());
                        if (typeId == null || !typeId.equals(query.machineryTypeId())) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }
}
