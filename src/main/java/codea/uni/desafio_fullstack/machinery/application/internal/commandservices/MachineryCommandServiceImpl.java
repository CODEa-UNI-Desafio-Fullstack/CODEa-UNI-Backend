package codea.uni.desafio_fullstack.machinery.application.internal.commandservices;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.*;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryCommandService;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MachineryCommandServiceImpl implements MachineryCommandService {
    private final MachineryRepository machineryRepository;
    private final MachineryTypeRepository machineryTypeRepository;

    public MachineryCommandServiceImpl(MachineryRepository machineryRepository, MachineryTypeRepository machineryTypeRepository) {
        this.machineryRepository = machineryRepository;
        this.machineryTypeRepository = machineryTypeRepository;
    }

    @Override
    public Optional<Machinery> handle(CreateMachineryCommand command) {
        var machineryType = this.machineryTypeRepository.findById(command.machineryTypeId())
                .orElseThrow(() -> new IllegalArgumentException("MachineryType not found for id: " + command.machineryTypeId()));
        var machinery = new Machinery(command, machineryType);

        this.machineryRepository.save(machinery);

        return Optional.of(machinery);
    }

    @Override
    public Optional<Machinery> handle(UpdateMachineryMachineryTypeCommand command) {
        var machinery = this.machineryRepository.findById(command.code())
                .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + command.code()));
        var machineryType = this.machineryTypeRepository.findById(command.machineryTypeId())
                .orElseThrow(() -> new IllegalArgumentException("MachineryType not found for id: " + command.machineryTypeId()));

        machinery.setMachineryType(machineryType);
        machinery.checkAndApplyMaintenanceThreshold();
        this.machineryRepository.save(machinery);
        return Optional.of(machinery);
    }

    @Override
    public Optional<Machinery> handle(UpdateMachineryHourMeterCommand command) {
        var machinery = this.machineryRepository.findByCodeWithType(command.code())
                .orElseGet(() -> this.machineryRepository.findById(command.code())
                        .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + command.code())));

        machinery.setHourMeter(command.hours());
        machinery.checkAndApplyMaintenanceThreshold();
        this.machineryRepository.save(machinery);
        return Optional.of(machinery);
    }

    @Override
    public Optional<Machinery> handle(RecordMachineryWorkedHoursCommand command) {
        var machinery = this.machineryRepository.findByCodeWithLock(command.code())
                .orElseGet(() -> this.machineryRepository.findByCodeWithType(command.code())
                        .orElseGet(() -> this.machineryRepository.findById(command.code())
                                .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + command.code()))));

        machinery.recordWorkedHours(command.workedHours());
        this.machineryRepository.save(machinery);
        return Optional.of(machinery);
    }

    @Override
    public Optional<Machinery> handle(ResetMachineryMaintenanceCommand command) {
        var machinery = this.machineryRepository.findById(command.code())
                .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + command.code()));

        machinery.resetAfterMaintenance();
        this.machineryRepository.save(machinery);
        return Optional.of(machinery);
    }

    @Override
    public Optional<Machinery> handle(ChangeMachineryStateCommand command) {
        var machinery = this.machineryRepository.findById(command.code())
                .orElseThrow(() -> new IllegalArgumentException("Machinery not found with code: " + command.code()));

        machinery.setState(command.state());
        this.machineryRepository.save(machinery);
        return Optional.of(machinery);
    }

    @Override
    public void handle(DeleteMachineryCommand command) {
        if (!this.machineryRepository.existsById(command.code())) {
            throw new IllegalArgumentException("Machinery not found with code: " + command.code());
        }
        this.machineryRepository.deleteById(command.code());
    }
}

