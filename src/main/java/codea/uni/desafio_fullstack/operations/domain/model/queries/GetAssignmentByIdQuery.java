package codea.uni.desafio_fullstack.operations.domain.model.queries;

import java.util.UUID;

public record GetAssignmentByIdQuery(UUID id) {
    public GetAssignmentByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
    }
}
