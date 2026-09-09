package codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform;

import codea.uni.desafio_fullstack.maintenance.domain.model.commands.UpdateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.UpdateMaintenanceResource;

import java.util.UUID;

public class UpdateMaintenanceCommandFromResourceAssembler {
    public static UpdateMaintenanceCommand toCommandFromResource(UUID id, UpdateMaintenanceResource resource) {
        return new UpdateMaintenanceCommand(
                id,
                resource.hourMeter(),
                resource.operatorId(),
                resource.observation()
        );
    }
}
