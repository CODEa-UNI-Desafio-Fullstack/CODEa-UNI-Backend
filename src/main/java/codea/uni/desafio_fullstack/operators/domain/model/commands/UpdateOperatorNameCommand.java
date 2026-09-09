package codea.uni.desafio_fullstack.operators.domain.model.commands;

import java.util.UUID;

public record UpdateOperatorNameCommand(UUID id, String name) {
    public UpdateOperatorNameCommand {
        if (id == null) {
            throw new IllegalArgumentException("Operator id cannot be null");
        }
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Operator name cannot be null or empty");
        }
    }
}
