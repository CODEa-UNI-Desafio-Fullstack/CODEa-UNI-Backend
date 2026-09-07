package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public record EndAssignmentCommand(
        UUID id,
        LocalTime timeEnd,
        LocalDateTime dateTimeEnd
) {
    public EndAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }

    public EndAssignmentCommand(UUID id, LocalTime timeEnd) {
        this(id, timeEnd, null);
    }

    public EndAssignmentCommand(UUID id, LocalDateTime dateTimeEnd) {
        this(id, null, dateTimeEnd);
    }
}
