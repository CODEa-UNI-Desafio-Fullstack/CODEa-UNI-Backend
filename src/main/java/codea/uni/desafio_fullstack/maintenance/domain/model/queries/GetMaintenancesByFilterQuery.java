package codea.uni.desafio_fullstack.maintenance.domain.model.queries;

import java.time.LocalDate;
import java.util.UUID;

public record GetMaintenancesByFilterQuery(
        UUID operatorId,
        String machineryCode,
        Integer machineryTypeId,
        LocalDate startDate,
        LocalDate endDate
) {
}
