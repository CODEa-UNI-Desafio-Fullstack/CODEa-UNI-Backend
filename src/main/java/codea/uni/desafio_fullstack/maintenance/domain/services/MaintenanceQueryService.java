package codea.uni.desafio_fullstack.maintenance.domain.services;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

public interface MaintenanceQueryService {
    List<Maintenance> handle(GetAllMaintenancesQuery query);
    Optional<Maintenance> handle(GetMaintenanceByIdQuery query);
    List<Maintenance> handle(GetMaintenancesByMachineryCodeQuery query);
    List<Maintenance> handle(GetMaintenancesByDateRangeQuery query);
    List<Maintenance> handle(GetMaintenancesByOperatorIdQuery query);
    List<Maintenance> handle(GetMaintenancesByFilterQuery query);
}
