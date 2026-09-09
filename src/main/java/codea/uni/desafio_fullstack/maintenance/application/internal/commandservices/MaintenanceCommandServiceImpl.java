package codea.uni.desafio_fullstack.maintenance.application.internal.commandservices;

import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.maintenance.application.internal.outboundservices.acl.ExternalOperatorService;
import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.DeleteMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.model.commands.UpdateMaintenanceCommand;
import codea.uni.desafio_fullstack.maintenance.domain.services.MaintenanceCommandService;
import codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories.MaintenanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MaintenanceCommandServiceImpl implements MaintenanceCommandService {

    private final MaintenanceRepository maintenanceRepository;
    private final ExternalMachineryService externalMachineryService;
    private final ExternalOperatorService externalOperatorService;

    public MaintenanceCommandServiceImpl(
            MaintenanceRepository maintenanceRepository,
            ExternalMachineryService externalMachineryService,
            ExternalOperatorService externalOperatorService) {
        this.maintenanceRepository = maintenanceRepository;
        this.externalMachineryService = externalMachineryService;
        this.externalOperatorService = externalOperatorService;
    }

    @Override
    public Optional<Maintenance> handle(CreateMaintenanceCommand command) {
        if (!this.externalMachineryService.existsMachineryByCode(command.machineryCode())) {
            throw new IllegalArgumentException("Machinery with code " + command.machineryCode() + " does not exist");
        }

        if (!this.externalOperatorService.existsOperatorById(command.operatorId())) {
            throw new IllegalArgumentException("Operator with id " + command.operatorId() + " does not exist");
        }

        var maintenance = new Maintenance(command);
        var savedMaintenance = this.maintenanceRepository.save(maintenance);
        this.externalMachineryService.resetMachineryAfterMaintenance(command.machineryCode());

        return Optional.of(savedMaintenance);
    }

    @Override
    public Optional<Maintenance> handle(UpdateMaintenanceCommand command) {
        var maintenanceOptional = this.maintenanceRepository.findById(command.id());
        if (maintenanceOptional.isEmpty()) {
            throw new IllegalArgumentException("Maintenance with id " + command.id() + " does not exist");
        }

        if (!this.externalOperatorService.existsOperatorById(command.operatorId())) {
            throw new IllegalArgumentException("Operator with id " + command.operatorId() + " does not exist");
        }

        var maintenance = maintenanceOptional.get();
        maintenance.updateDetails(command.hourMeter(), command.operatorId(), command.observation());
        var updatedMaintenance = this.maintenanceRepository.save(maintenance);
        return Optional.of(updatedMaintenance);
    }

    @Override
    public void handle(DeleteMaintenanceCommand command) {
        if (!this.maintenanceRepository.existsById(command.id())) {
            throw new IllegalArgumentException("Maintenance with id " + command.id() + " does not exist");
        }
        this.maintenanceRepository.deleteById(command.id());
    }
}
