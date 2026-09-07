package codea.uni.desafio_fullstack.operations.domain.model.queries;

import java.time.LocalDate;

public record GetShiftsByFilterQuery(LocalDate date, Boolean shiftType) {
}
