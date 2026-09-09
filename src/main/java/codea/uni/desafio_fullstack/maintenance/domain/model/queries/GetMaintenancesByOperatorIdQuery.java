package codea.uni.desafio_fullstack.maintenance.domain.model.queries;

import java.util.UUID;

public record GetMaintenancesByOperatorIdQuery(UUID operatorId) {
    public GetMaintenancesByOperatorIdQuery {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }
}
