package codea.uni.desafio_fullstack.maintenance.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMaintenanceCommand(
        String machineryCode,
        LocalDate date,
        float hourMeter,
        UUID operatorId,
        String observation
) {
    public CreateMaintenanceCommand {
        if (machineryCode == null || machineryCode.isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Maintenance date cannot be null");
        }
        if (hourMeter < 0) {
            throw new IllegalArgumentException("Hour meter cannot be negative");
        }
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }
}
