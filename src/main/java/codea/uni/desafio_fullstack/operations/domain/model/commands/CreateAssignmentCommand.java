package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.util.UUID;

public record CreateAssignmentCommand(
        UUID operatorId,
        String machineryCode,
        UUID shiftId
) {
    public CreateAssignmentCommand {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
        if (machineryCode == null || machineryCode.isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or blank");
        }
        if (shiftId == null) {
            throw new IllegalArgumentException("Shift ID cannot be null");
        }
    }
}
