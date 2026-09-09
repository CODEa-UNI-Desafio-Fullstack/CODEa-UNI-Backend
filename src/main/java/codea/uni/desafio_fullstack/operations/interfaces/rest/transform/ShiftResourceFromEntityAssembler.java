package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.ShiftResource;

public class ShiftResourceFromEntityAssembler {
    public static ShiftResource toResourceFromEntity(Shift entity) {
        String shift ="";
        if(entity.getShiftType()){
            shift = "Dia";
        }
        else{
            shift = "Noche";
        }
        return new ShiftResource(
                entity.getId(),
                entity.getDate(),
                shift,
                entity.getDuration()
        );
    }
}
