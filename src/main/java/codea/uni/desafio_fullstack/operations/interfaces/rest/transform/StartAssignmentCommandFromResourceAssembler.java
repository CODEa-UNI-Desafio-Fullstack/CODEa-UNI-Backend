package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.StartAssignmentCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.StartAssignmentResource;

import java.util.UUID;

public class StartAssignmentCommandFromResourceAssembler {
    public static StartAssignmentCommand toCommandFromResource(UUID id, StartAssignmentResource resource) {
        var timeStart = (resource != null) ? resource.timeStart() : null;
        return new StartAssignmentCommand(id, timeStart);
    }
}
