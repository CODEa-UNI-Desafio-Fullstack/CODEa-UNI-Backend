package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.UpdateMachineryMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.UpdateMachineryMachineryTypeResource;

public class UpdateMachineryMachineryTypeCommandFromResourceAssembler {
    public static UpdateMachineryMachineryTypeCommand toCommandFromResource(UpdateMachineryMachineryTypeResource resource) {
        return new UpdateMachineryMachineryTypeCommand(resource.code(), resource.machineryTypeId());
    }
}
