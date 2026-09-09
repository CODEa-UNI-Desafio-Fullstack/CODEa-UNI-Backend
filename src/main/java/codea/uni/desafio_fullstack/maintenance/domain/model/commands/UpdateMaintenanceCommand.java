package codea.uni.desafio_fullstack.maintenance.domain.model.commands;

import java.util.UUID;

public record UpdateMaintenanceCommand(
        UUID id,
        float hourMeter,
        UUID operatorId,
        String observation
) {
    public UpdateMaintenanceCommand {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance ID cannot be null");
        }
        if (hourMeter < 0) {
            throw new IllegalArgumentException("Hour meter cannot be negative");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }
}
