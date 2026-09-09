package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.CreateMachineryTypeResource;

public class CreateMachineryTypeCommandFromResourceAssembler {
    public static CreateMachineryTypeCommand toCommandFromResource(CreateMachineryTypeResource resource) {
        return new CreateMachineryTypeCommand(resource.name(), resource.maintenanceTime());
    }
}
