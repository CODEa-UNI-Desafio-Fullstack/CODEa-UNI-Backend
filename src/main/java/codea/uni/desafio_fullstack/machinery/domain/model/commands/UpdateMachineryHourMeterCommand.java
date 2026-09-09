package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record UpdateMachineryHourMeterCommand(String code, float hours) {
    public UpdateMachineryHourMeterCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code cannot be null or empty");
        }
        if (hours < 0) {
            throw new IllegalArgumentException("Hours must be non-negative");
        }
    }
}
