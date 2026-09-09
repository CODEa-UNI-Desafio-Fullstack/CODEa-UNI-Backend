package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.UUID;

public record MachineryMaintenanceProjectionResource(
        String machineryCode,
        Integer machineryTypeId,
        String machineryTypeName,
        float currentHourMeter,
        int maintenanceThreshold,
        float remainingHours,
        float projectedHours,
        float differenceHours,
        LocalDate estimatedThresholdDate,
        UUID estimatedThresholdShiftId,
        String estimatedThresholdShiftType
) {
}
