package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.util.UUID;

public record CreateAssignmentResource(
        UUID operatorId,
        String machineryCode,
        UUID shiftId
) {
}
