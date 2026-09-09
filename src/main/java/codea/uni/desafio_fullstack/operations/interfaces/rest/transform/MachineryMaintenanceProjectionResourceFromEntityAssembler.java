package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.MachineryMaintenanceProjection;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.MachineryMaintenanceProjectionResource;

public class MachineryMaintenanceProjectionResourceFromEntityAssembler {
    public static MachineryMaintenanceProjectionResource toResourceFromEntity(MachineryMaintenanceProjection projection) {
        return new MachineryMaintenanceProjectionResource(
                projection.machineryCode(),
                projection.machineryTypeId(),
                projection.machineryTypeName(),
                projection.currentHourMeter(),
                projection.maintenanceThreshold(),
                projection.remainingHours(),
                projection.projectedHours(),
                projection.differenceHours(),
                projection.estimatedThresholdDate(),
                projection.estimatedThresholdShiftId(),
                projection.estimatedThresholdShiftType()
        );
    }
}
