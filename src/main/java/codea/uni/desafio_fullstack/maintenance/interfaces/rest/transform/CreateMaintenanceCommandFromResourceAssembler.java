package codea.uni.desafio_fullstack.maintenance.interfaces.rest.transform;

import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources.CreateMaintenanceResource;

public class CreateMaintenanceCommandFromResourceAssembler {
    public static CreateMaintenanceCommand toCommandFromResource(CreateMaintenanceResource resource) {
        return new CreateMaintenanceCommand(
                resource.machineryCode(),
                resource.date(),
                resource.hourMeter(),
                resource.operatorId(),
                resource.observation()
        );
    }
}
