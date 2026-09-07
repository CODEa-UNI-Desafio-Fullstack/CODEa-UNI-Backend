package codea.uni.desafio_fullstack.operations.domain.model.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class AssignmentValidationException extends RuntimeException{
    private final String code;
    private final List<AssignmentValidationError> errors;

    public AssignmentValidationException(List<AssignmentValidationError> errors) {
        super("Assignment rejected due to validation constraints");
        this.code = "Assignment Rejected";
        this.errors = errors;
    }

    public AssignmentValidationException(String rule, String message) {
        this(List.of(new AssignmentValidationError(rule, message)));
    }
}
