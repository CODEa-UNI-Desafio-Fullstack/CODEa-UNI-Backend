package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDate;

public record CreateShiftResource(
        LocalDate date,
        Boolean shiftType,
        Integer duration
) {
}
