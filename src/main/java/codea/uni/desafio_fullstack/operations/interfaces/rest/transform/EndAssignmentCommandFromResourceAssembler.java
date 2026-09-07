package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.EndAssignmentCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.EndAssignmentResource;

import java.util.UUID;

public class EndAssignmentCommandFromResourceAssembler {
    public static EndAssignmentCommand toCommandFromResource(UUID id, EndAssignmentResource resource) {
        var timeEnd = (resource != null) ? resource.timeEnd() : null;
        return new EndAssignmentCommand(id, timeEnd);
    }
}
