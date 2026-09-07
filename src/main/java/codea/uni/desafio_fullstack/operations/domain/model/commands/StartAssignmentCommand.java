package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record StartAssignmentCommand(
        UUID id,
        LocalTime timeStart,
        LocalDateTime dateTimeStart
) {
    public StartAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }

    public StartAssignmentCommand(UUID id, LocalTime timeStart) {
        this(id, timeStart, null);
    }

    public StartAssignmentCommand(UUID id, LocalDateTime dateTimeStart) {
        this(id, null, dateTimeStart);
    }
}
