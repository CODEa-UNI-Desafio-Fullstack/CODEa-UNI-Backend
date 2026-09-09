package codea.uni.desafio_fullstack.operations.domain.model.exceptions;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;

public record AssignmentValidationContext(CreateAssignmentCommand command,
                                          Shift shift,
                                          Integer machineryTypeId) {
}
