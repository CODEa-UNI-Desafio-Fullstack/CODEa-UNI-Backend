package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.UpdateMachineryCertificationResource;

import java.util.UUID;

public class UpdateMachineryCertificationCommandFromResourceAssembler {
    public static UpdateMachineryCertificationCommand toCommandFromResource(UUID operatorId, Integer machineryTypeId, UpdateMachineryCertificationResource resource) {
        return new UpdateMachineryCertificationCommand(
                operatorId,
                machineryTypeId,
                resource.expirationDate()
        );
    }
}
