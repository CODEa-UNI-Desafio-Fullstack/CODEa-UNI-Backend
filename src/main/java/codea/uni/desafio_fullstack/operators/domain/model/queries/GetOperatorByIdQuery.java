package codea.uni.desafio_fullstack.operators.domain.model.queries;

import java.util.UUID;

public record GetOperatorByIdQuery(UUID id) {
    public GetOperatorByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Operator id cannot be null");
        }
    }
}
