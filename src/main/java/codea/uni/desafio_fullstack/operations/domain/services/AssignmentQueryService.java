package codea.uni.desafio_fullstack.operations.domain.services;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetMachineryMaintenanceProjectionsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.AssignmentDetail;
import codea.uni.desafio_fullstack.operations.domain.model.valueobjects.MachineryMaintenanceProjection;

import java.util.List;
import java.util.Optional;

public interface AssignmentQueryService {
    Optional<Assignment> handle(GetAssignmentByIdQuery query);
    List<MachineryMaintenanceProjection> handle(GetMachineryMaintenanceProjectionsQuery query);
    List<AssignmentDetail> handle(GetAssignmentsByFilterQuery query);
}
