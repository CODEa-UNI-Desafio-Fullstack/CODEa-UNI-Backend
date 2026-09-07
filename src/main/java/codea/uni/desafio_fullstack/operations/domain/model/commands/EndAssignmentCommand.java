package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public record EndAssignmentCommand(
        UUID id,
        LocalDateTime timeEnd
) {
    public EndAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }
}
