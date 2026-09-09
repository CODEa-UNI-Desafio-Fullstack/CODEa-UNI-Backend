package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record UpdateMachineryMachineryTypeCommand(String code, Integer machineryTypeId) {
    public UpdateMachineryMachineryTypeCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code cannot be null or empty");
        }
        if (machineryTypeId == null) {
            throw new IllegalArgumentException("machineryTypeId cannot be null");
        }
    }
}

