package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResource(
        UUID id,
        UUID operatorId,
        String machineryCode,
        UUID shiftId,
        LocalDateTime timeStart,
        LocalDateTime timeEnd,
        Integer actualShiftTime
) {
}
