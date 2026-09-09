package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDate;

public record UpdateShiftResource(
        LocalDate date,
        Boolean shiftType,
        Integer duration
) {
}
