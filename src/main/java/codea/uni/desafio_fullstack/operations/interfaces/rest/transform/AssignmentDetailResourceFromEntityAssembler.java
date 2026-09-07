package codea.uni.desafio_fullstack.operations.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.AssignmentDetail;
import codea.uni.desafio_fullstack.operations.interfaces.rest.resources.AssignmentDetailResource;

public class AssignmentDetailResourceFromEntityAssembler {
    public static AssignmentDetailResource toResourceFromEntity(AssignmentDetail detail) {
        return new AssignmentDetailResource(
                detail.id(),
                detail.operatorId(),
                detail.operatorName(),
                detail.machineryCode(),
                detail.machineryTypeId(),
                detail.machineryTypeName(),
                detail.shiftId(),
                detail.shiftDate(),
                detail.shiftType(),
                detail.shiftDuration(),
                detail.timeStart(),
                detail.timeEnd(),
                detail.actualShiftTime()
        );
    }
}
