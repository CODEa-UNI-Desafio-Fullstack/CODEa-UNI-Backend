package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.AssignmentResource;

public class AssignmentResourceFromEntityAssembler {
    public static AssignmentResource toResourceFromEntity(Assignment entity) {
        return new AssignmentResource(
                entity.getId(),
                entity.getOperatorId(),
                entity.getMachineryCode(),
                entity.getShiftId(),
                entity.getTimeStart(),
                entity.getTimeEnd(),
                entity.getActualShiftTime()
        );
    }
}
