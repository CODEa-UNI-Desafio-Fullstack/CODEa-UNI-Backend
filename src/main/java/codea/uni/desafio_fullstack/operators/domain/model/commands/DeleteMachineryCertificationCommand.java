package codea.uni.desafio_fullstack.operators.domain.model.commands;

import java.util.UUID;

public record DeleteMachineryCertificationCommand(UUID operatorId, Integer machineryTypeId) {
    public DeleteMachineryCertificationCommand {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
        if (machineryTypeId == null || machineryTypeId <= 0) {
            throw new IllegalArgumentException("Machinery type ID must be a positive integer");
        }
    }
}
