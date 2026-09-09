package codea.uni.desafio_fullstack.operations.domain.model.valueobjects;

import java.time.LocalDate;
import java.util.UUID;

public record MachineryMaintenanceProjection(
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
