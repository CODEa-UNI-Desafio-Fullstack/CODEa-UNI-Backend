package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record UpdateMachineryTypeCommand(Integer id, String name, int maintenanceTime) {
    public UpdateMachineryTypeCommand {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (maintenanceTime <= 0) {
            throw new IllegalArgumentException("maintenanceTime must be positive");
        }
    }
}

