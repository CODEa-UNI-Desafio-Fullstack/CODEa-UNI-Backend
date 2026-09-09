package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateOperatorNameCommand;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.UpdateOperatorNameResource;

import java.util.UUID;

public class UpdateOperatorNameCommandFromResourceAssembler {
    public static UpdateOperatorNameCommand toCommandFromResource(UUID id, UpdateOperatorNameResource resource) {
        return new UpdateOperatorNameCommand(id, resource.name());
    }
}
