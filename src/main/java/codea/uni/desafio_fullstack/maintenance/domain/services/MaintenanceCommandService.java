package codea.uni.desafio_fullstack.maintenance.domain.services;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.DeleteMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.UpdateMaintenanceCommand;

import java.util.Optional;

public interface MaintenanceCommandService {
    Optional<Maintenance> handle(CreateMaintenanceCommand command);
    Optional<Maintenance> handle(UpdateMaintenanceCommand command);
    void handle(DeleteMaintenanceCommand command);
}
