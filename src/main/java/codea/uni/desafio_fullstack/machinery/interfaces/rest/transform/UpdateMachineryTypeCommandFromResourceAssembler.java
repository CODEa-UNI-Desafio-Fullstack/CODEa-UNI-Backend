package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.UpdateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.UpdateMachineryTypeResource;

public class UpdateMachineryTypeCommandFromResourceAssembler {
    public static UpdateMachineryTypeCommand toCommandFromResource(UpdateMachineryTypeResource resource) {
        return new UpdateMachineryTypeCommand(resource.id(), resource.name(), resource.maintenanceTime());
    }

    public static UpdateMachineryTypeCommand toCommandFromResource(Integer id, UpdateMachineryTypeResource resource) {
        return new UpdateMachineryTypeCommand(id, resource.name(), resource.maintenanceTime());
    }
}
