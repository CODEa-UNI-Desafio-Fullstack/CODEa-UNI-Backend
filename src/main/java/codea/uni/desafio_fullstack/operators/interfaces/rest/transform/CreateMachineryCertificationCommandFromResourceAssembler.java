package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.CreateMachineryCertificationResource;

import java.util.UUID;

public class CreateMachineryCertificationCommandFromResourceAssembler {
    public static CreateMachineryCertificationCommand toCommandFromResource(UUID operatorId, CreateMachineryCertificationResource resource) {
        return new CreateMachineryCertificationCommand(
                operatorId,
                resource.machineryTypeId(),
                resource.expirationDate()
        );
    }
}
