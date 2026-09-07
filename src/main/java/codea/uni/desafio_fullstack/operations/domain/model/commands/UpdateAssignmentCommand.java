package codea.uni.desafio_fullstack.operations.domain.model.commands;

import java.util.UUID;

public record UpdateAssignmentCommand(
        UUID id,
        UUID operatorId,
        String machineryCode,
        UUID shiftId
) {
    public UpdateAssignmentCommand {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
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
