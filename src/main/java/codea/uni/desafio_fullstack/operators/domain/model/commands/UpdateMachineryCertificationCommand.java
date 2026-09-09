package codea.uni.desafio_fullstack.operators.domain.model.commands;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateMachineryCertificationCommand(UUID operatorId, Integer machineryTypeId, LocalDate expirationDate) {
    public UpdateMachineryCertificationCommand {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
        if (machineryTypeId == null || machineryTypeId <= 0) {
            throw new IllegalArgumentException("Machinery type ID must be a positive integer");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
    }
}
