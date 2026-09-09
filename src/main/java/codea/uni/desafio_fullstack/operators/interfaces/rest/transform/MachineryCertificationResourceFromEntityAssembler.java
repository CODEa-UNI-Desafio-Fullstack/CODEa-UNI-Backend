package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.MachineryCertificationResource;

public class MachineryCertificationResourceFromEntityAssembler {
    public static MachineryCertificationResource toResourceFromEntity(MachineryCertification entity) {
        return new MachineryCertificationResource(
                entity.getId().getOperatorId(),
                entity.getId().getMachineryType(),
                entity.getExpirationDate()
        );
    }
}
