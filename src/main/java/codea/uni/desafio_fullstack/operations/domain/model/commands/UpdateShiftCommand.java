package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateShiftCommand(
        UUID id,
        LocalDate date,
        Boolean shiftType,
        Integer duration
) {
    public UpdateShiftCommand {
        if (id == null) {
            throw new IllegalArgumentException("Shift ID cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Shift date cannot be null");
        }
        if (shiftType == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Shift duration must be greater than zero");
        }
    }
}
