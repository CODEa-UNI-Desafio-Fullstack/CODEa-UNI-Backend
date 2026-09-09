package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.MachineryTypeResource;

public class MachineryTypeResourceFromEntityAssembler {
    public static MachineryTypeResource toResourceFromEntity(MachineryType entity) {
        return new MachineryTypeResource(entity.getId(), entity.getName(), entity.getMaintenanceTime());
    }
}
