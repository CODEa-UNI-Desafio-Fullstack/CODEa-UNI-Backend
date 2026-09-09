package codea.uni.desafio_fullstack.machinery.interfaces.rest.transform;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.interfaces.rest.resources.MachineryResource;

//Lo que se muestra al usuario


public class MachineryResourceFromEntityAssembler {
    public static MachineryResource toResourceFromEntity(Machinery entity) {

        String state;

        if(entity.isState()){
            state = "ACTIVE";
        }
        else{state = "BLOCKED";}

        return new MachineryResource(
                entity.getCode(),
                entity.getMachineryType().getName(),
                entity.getHourMeter(),
                state
                );
    }
}
