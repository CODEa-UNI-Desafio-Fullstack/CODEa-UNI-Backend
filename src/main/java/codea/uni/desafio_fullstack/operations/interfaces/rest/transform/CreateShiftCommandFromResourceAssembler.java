package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.CreateShiftResource;

public class CreateShiftCommandFromResourceAssembler {
    public static CreateShiftCommand toCommandFromResource(CreateShiftResource resource) {
        return new CreateShiftCommand(
                resource.date(),
                resource.shiftType(),
                resource.duration()
        );
    }
}
