package codea.uni.desafio_fullstack.operations.domain.model.exceptions;

import java.util.Optional;

public interface AssignmentValidationRule {
    Optional<AssignmentValidationError> validate(AssignmentValidationContext context);
}
