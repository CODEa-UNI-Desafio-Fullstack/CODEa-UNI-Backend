package codea.uni.desafio_fullstack.operations.domain.services;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import codea.uni.desafio_fullstack.operations.domain.model.commands.*;

import java.util.Optional;

public interface AssignmentCommandService {
    Optional<Assignment> handle(CreateAssignmentCommand command);
    Optional<Assignment> handle(UpdateAssignmentCommand command);
    Optional<Assignment> handle(StartAssignmentCommand command);
    Optional<Assignment> handle(EndAssignmentCommand command);
    void handle(DeleteAssignmentCommand command);
}
