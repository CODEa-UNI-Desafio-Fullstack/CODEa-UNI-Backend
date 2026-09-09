package codea.uni.desafio_fullstack.operators.domain.services;

import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.DeleteMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;

import java.util.Optional;

public interface MachineryCertificationCommandService {
    Optional<MachineryCertification> handle(CreateMachineryCertificationCommand command);
    Optional<MachineryCertification> handle(UpdateMachineryCertificationCommand command);
    void handle(DeleteMachineryCertificationCommand command);
}
