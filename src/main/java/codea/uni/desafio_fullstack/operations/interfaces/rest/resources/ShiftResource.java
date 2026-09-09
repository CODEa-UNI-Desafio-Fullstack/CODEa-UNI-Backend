package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.UUID;

public record ShiftResource(
        UUID id,
        LocalDate date,
        String shiftType,
        Integer duration
) {
}
