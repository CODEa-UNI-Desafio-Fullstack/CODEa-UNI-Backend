package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.CreateAssignmentResource;

public class CreateAssignmentCommandFromResourceAssembler {
    public static CreateAssignmentCommand toCommandFromResource(CreateAssignmentResource resource) {
        return new CreateAssignmentCommand(
                resource.operatorId(),
                resource.machineryCode(),
                resource.shiftId()
        );
    }
}
