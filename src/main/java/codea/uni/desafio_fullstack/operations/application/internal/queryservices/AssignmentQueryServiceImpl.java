package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAssignmentByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.services.AssignmentQueryService;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AssignmentQueryServiceImpl implements AssignmentQueryService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentQueryServiceImpl(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public Optional<Assignment> handle(GetAssignmentByIdQuery query) {
        return this.assignmentRepository.findById(query.id());
    }
}
