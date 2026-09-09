package codea.uni.desafio_fullstack.maintenance.domain.model.queries;

import java.time.LocalDate;

public record GetMaintenancesByDateRangeQuery(LocalDate startDate, LocalDate endDate) {
    public GetMaintenancesByDateRangeQuery {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }
}
