package codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.MaintenanceResource;

public class MaintenanceResourceFromEntityAssembler {
    public static MaintenanceResource toResourceFromEntity(Maintenance entity) {
        return new MaintenanceResource(
                entity.getId(),
                entity.getMachineryCode(),
                entity.getDate(),
                entity.getHourMeter(),
                entity.getOperatorId(),
                entity.getObservation()
        );
    }
}
