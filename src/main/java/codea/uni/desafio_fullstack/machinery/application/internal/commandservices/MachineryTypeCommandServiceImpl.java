package codea.uni.desafio_fullstack.machinery.application.internal.commandservices;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.DeleteMachineryTypeCommmand;
import codea.uni.desafio_fullstack.machinery.domain.model.commands.UpdateMachineryTypeCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeCommandService;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MachineryTypeCommandServiceImpl implements MachineryTypeCommandService {
    private final MachineryTypeRepository machineryTypeRepository;

    public MachineryTypeCommandServiceImpl(MachineryTypeRepository machineryTypeRepository) {
        this.machineryTypeRepository = machineryTypeRepository;
    }

    @Override
    public Optional<MachineryType> handle(CreateMachineryTypeCommand command) {
        var machineryType = new MachineryType(command);
        this.machineryTypeRepository.save(machineryType);
        return Optional.of(machineryType);
    }

    @Override
    public Optional<MachineryType> handle(UpdateMachineryTypeCommand command) {
        var machineryType = this.machineryTypeRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("MachineryType not found for id: " + command.id()));

        machineryType.setName(command.name());
        machineryType.setMaintenanceTime(command.maintenanceTime());
        this.machineryTypeRepository.save(machineryType);

        return Optional.of(machineryType);
    }

    @Override
    public void handle(DeleteMachineryTypeCommmand command) {
        if (!this.machineryTypeRepository.existsById(command.typeId())) {
            throw new IllegalArgumentException("MachineryType not found for id: " + command.typeId());
        }
        this.machineryTypeRepository.deleteById(command.typeId());
    }
}
