package codea.uni.desafio_fullstack.machinery.domain.services;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.DeleteMachineryTypeCommmand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.UpdateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;

import java.util.Optional;

public interface MachineryTypeCommandService {
    Optional<MachineryType> handle(CreateMachineryTypeCommand command);
    Optional<MachineryType> handle(UpdateMachineryTypeCommand command);
    void handle(DeleteMachineryTypeCommmand command);
}
