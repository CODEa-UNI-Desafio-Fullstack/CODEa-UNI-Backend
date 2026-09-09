package codea.uni.desafio_fullstack.operations.domain.model.exceptions;

import java.util.List;

public record AssignmentValidationResult(boolean isValid, List<AssignmentValidationError> errors) {
    public static AssignmentValidationResult valid() { return new AssignmentValidationResult(true, List.of()); }
    public static AssignmentValidationResult invalid(List<AssignmentValidationError> errors) { return new AssignmentValidationResult(false, errors); }
}
