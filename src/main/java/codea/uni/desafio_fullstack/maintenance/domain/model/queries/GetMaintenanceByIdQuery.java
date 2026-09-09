package codea.uni.desafio_fullstack.maintenance.domain.model.queries;

import java.util.UUID;

public record GetMaintenanceByIdQuery(UUID id) {
    public GetMaintenanceByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("Maintenance ID cannot be null");
        }
    }
}
