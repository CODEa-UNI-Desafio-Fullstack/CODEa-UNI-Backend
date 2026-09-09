package codea.uni.desafio_fullstack.machinery.domain.services;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.*;

import java.util.Optional;

public interface MachineryCommandService {
    Optional<Machinery> handle(CreateMachineryCommand command);
    Optional<Machinery> handle(UpdateMachineryMachineryTypeCommand command);
    Optional<Machinery> handle(UpdateMachineryHourMeterCommand command);
    Optional<Machinery> handle(RecordMachineryWorkedHoursCommand command);
    Optional<Machinery> handle(ResetMachineryMaintenanceCommand command);
    Optional<Machinery> handle(ChangeMachineryStateCommand command);
    void handle(DeleteMachineryCommand command);
}
