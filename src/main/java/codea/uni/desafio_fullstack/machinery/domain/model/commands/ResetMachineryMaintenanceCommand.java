package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record ResetMachineryMaintenanceCommand(String code) {
    public ResetMachineryMaintenanceCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or empty");
        }
    }
}
