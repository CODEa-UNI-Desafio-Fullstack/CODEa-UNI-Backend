package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateAssignmentCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.UpdateAssignmentResource;

import java.util.UUID;

public class UpdateAssignmentCommandFromResourceAssembler {
    public static UpdateAssignmentCommand toCommandFromResource(UUID id, UpdateAssignmentResource resource) {
        return new UpdateAssignmentCommand(
                id,
                resource.operatorId(),
                resource.machineryCode(),
                resource.shiftId()
        );
    }
}
