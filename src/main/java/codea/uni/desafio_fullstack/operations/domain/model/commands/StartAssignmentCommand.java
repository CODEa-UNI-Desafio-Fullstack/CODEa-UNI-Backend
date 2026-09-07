package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.time.LocalDateTime;
import java.util.UUID;

public record StartAssignmentCommand(
        UUID id,
        LocalDateTime timeStart
) {
    public StartAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }
}
