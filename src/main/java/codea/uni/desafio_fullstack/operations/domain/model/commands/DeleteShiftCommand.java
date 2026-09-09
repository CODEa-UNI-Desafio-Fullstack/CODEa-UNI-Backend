package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.util.UUID;

public record DeleteShiftCommand(UUID id) {
    public DeleteShiftCommand {
        if (id == null) {
            throw new IllegalArgumentException("Shift ID cannot be null");
        }
    }
}
