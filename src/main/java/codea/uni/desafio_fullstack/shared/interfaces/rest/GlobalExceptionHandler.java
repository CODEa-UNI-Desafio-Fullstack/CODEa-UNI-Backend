package codea.uni.desafio_fullstack.shared.interfaces.rest;

import codea.uni.desafio_fullstack.operations.domain.model.exceptions.AssignmentValidationException;
import codea.uni.desafio_fullstack.shared.interfaces.rest.resources.MessageResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AssignmentValidationException.class)
    public ResponseEntity<Map<String, Object>> handleAssignmentValidationException(AssignmentValidationException e) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "ASSIGNMENT_REJECTED");
        response.put("message", e.getMessage());
        response.put("errors", e.getErrors());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResource> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new MessageResource(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResource> handleGeneralException(Exception ex) {
        return ResponseEntity.internalServerError().body(new MessageResource("An internal server error occurred"));
    }
}
