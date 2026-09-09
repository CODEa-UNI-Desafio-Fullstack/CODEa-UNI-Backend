package codea.uni.desafio_fullstack.operators.domain.model.commands;

public record CreateOperatorCommand(String name) {
    public CreateOperatorCommand {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Operator name cannot be null or empty");
        }
    }
}
