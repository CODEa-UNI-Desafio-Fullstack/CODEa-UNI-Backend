package codea.uni.desafio_fullstack.operations.domain.model.queries;

import java.util.UUID;

public record GetShiftByIdQuery(UUID id) {
    public GetShiftByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Shift ID cannot be null");
        }
    }
}
