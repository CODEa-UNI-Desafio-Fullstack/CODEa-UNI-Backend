package codea.uni.desafio_fullstack.machinery.domain.model.commands;

public record RecordMachineryWorkedHoursCommand(String code, float workedHours) {
    public RecordMachineryWorkedHoursCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or empty");
        }
        if (workedHours <= 0) {
            throw new IllegalArgumentException("Worked hours must be greater than zero");
        }
    }
}
