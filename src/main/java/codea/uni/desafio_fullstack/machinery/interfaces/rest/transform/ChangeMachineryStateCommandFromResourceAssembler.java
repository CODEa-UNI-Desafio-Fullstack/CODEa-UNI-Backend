package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.ChangeMachineryStateCommand;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.ChangeMachineryStateResource;

public class ChangeMachineryStateCommandFromResourceAssembler {
    public static ChangeMachineryStateCommand toCommandFromResource(ChangeMachineryStateResource resource) {
        return new ChangeMachineryStateCommand(resource.code(), resource.state());
    }
}
