package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.util.UUID;

public record DeleteAssignmentCommand(UUID id) {
    public DeleteAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }
}
