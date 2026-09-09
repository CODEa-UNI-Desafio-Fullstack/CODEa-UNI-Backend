package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.CreateMachineryResource;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.MachineryResource;

public class CreateMachineryCommandFromResourceAssembler {
    public static CreateMachineryCommand toCommandFromResource(CreateMachineryResource resource) {
        return new CreateMachineryCommand(resource.code(), resource.MachineryTypeId());
    }
}
