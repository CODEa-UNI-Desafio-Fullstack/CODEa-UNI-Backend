package codea.uni.desafio_fullstack.operators.domain.model.queries;

import java.util.UUID;

public record GetAllCertificationsByOperatorIdQuery(UUID operatorId) {
    public GetAllCertificationsByOperatorIdQuery {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }
}
