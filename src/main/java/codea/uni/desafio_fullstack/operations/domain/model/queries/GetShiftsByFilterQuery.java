package codea.uni.desafio_fullstack.operations.domain.model.queries;

import java.time.LocalDate;

public record GetShiftsByFilterQuery(LocalDate date, Boolean shiftType) {
    public GetShiftsByFilterQuery {
        if (date == null && shiftType == null) {
            throw new IllegalArgumentException("At least one filter parameter (date or shiftType) must be provided");
        }
    }
}
