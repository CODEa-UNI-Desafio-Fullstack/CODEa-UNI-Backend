package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record ChangeMachineryStateCommand(String code, boolean state) {
    public ChangeMachineryStateCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code cannot be null or empty");
        }
    }
}
