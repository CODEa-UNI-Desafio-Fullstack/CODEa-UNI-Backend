package codea.uni.desafio_fullstack.operations.domain.services;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAllShiftsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftsByFilterQuery;

import java.util.List;
import java.util.Optional;

public interface ShiftQueryService {
    List<Shift> handle(GetAllShiftsQuery query);
    Optional<Shift> handle(GetShiftByIdQuery query);
    List<Shift> handle(GetShiftsByFilterQuery query);
}
