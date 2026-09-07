package codea.uni.desafio_fullstack.operations.domain.services;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;

import java.util.Optional;

public interface AssignmentQueryService {
    Optional<Assignment> handle(GetAssignmentByIdQuery query);
}
