package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentDetailResource(
        UUID id,
        UUID operatorId,
        String operatorName,
        String machineryCode,
        Integer machineryTypeId,
        String machineryTypeName,
        UUID shiftId,
        LocalDate shiftDate,
        Boolean shiftType,
        Integer shiftDuration,
        LocalDateTime timeStart,
        LocalDateTime timeEnd,
        Integer actualShiftTime
) {
}
