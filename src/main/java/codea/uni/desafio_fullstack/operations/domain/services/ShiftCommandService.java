package codea.uni.desafio_fullstack.operations.domain.services;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateShiftCommand;

import java.util.Optional;

public interface ShiftCommandService {
    Optional<Shift> handle(CreateShiftCommand command);
    Optional<Shift> handle(UpdateShiftCommand command);
    void handle(DeleteShiftCommand command);
}
