package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateShiftCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.UpdateShiftResource;

import java.util.UUID;

public class UpdateShiftCommandFromResourceAssembler {
    public static UpdateShiftCommand toCommandFromResource(UUID id, UpdateShiftResource resource) {
        return new UpdateShiftCommand(
                id,
                resource.date(),
                resource.shiftType(),
                resource.duration()
        );
    }
}
