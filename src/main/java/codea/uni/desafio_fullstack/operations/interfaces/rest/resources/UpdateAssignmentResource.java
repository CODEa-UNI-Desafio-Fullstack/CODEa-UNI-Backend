package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.util.UUID;

public record UpdateAssignmentResource(
        UUID operatorId,
        String machineryCode,
        UUID shiftId
) {
}
