package codea.uni.desafio_fullstack.operators.domain.model.queries;

import java.util.UUID;

public record GetCertificationByOperatorIdAndMachineryTypeIdQuery(UUID operatorId, Integer machineryTypeId) {
    public GetCertificationByOperatorIdAndMachineryTypeIdQuery {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
        if (machineryTypeId == null || machineryTypeId <= 0) {
            throw new IllegalArgumentException("Machinery type ID must be a positive integer");
        }
    }
}
