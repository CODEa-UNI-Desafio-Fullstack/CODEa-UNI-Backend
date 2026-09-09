package codea.uni.desafio_fullstack.maintenance.domain.model.commands;

import java.util.UUID;

public record DeleteMaintenanceCommand(UUID id) {
    public DeleteMaintenanceCommand {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance ID cannot be null");
        }
    }
}
