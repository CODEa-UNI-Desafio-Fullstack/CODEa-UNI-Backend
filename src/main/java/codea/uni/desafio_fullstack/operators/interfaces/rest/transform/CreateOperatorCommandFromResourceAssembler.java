package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.CreateOperatorResource;

public class CreateOperatorCommandFromResourceAssembler {
    public static CreateOperatorCommand toCommandFromResource(CreateOperatorResource resource) {
        return new CreateOperatorCommand(resource.name());
    }
}
