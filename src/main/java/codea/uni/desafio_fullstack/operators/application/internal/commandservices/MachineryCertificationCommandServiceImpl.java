package codea.uni.desafio_fullstack.operators.application.internal.commandservices;

import codea.uni.desafio_fullstack.operators.application.internal.outboundservices.acl.ExternalMachineryService;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.DeleteMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateMachineryCertificationCommand;
import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.valueobjects.MachineryCertificationId;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationCommandService;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MachineryCertificationCommandServiceImpl implements MachineryCertificationCommandService {

    private final OperatorRepository operatorRepository;
    private final MachineryCertificationRepository machineryCertificationRepository;
    private final ExternalMachineryService externalMachineryService;

    public MachineryCertificationCommandServiceImpl(OperatorRepository operatorRepository,
                                                   MachineryCertificationRepository machineryCertificationRepository,
                                                   ExternalMachineryService externalMachineryService) {
        this.operatorRepository = operatorRepository;
        this.machineryCertificationRepository = machineryCertificationRepository;
        this.externalMachineryService = externalMachineryService;
    }

    @Override
    public Optional<MachineryCertification> handle(CreateMachineryCertificationCommand command) {
        var operator = this.operatorRepository.findById(command.operatorId())
                .orElseThrow(() -> new IllegalArgumentException("Operator not found with id: " + command.operatorId()));

        if (!this.externalMachineryService.existsMachineryTypeById(command.machineryTypeId())) {
            throw new IllegalArgumentException("Machinery type not found with id: " + command.machineryTypeId());
        }

        var certificationId = new MachineryCertificationId(command.operatorId(), command.machineryTypeId());
        if (this.machineryCertificationRepository.existsById(certificationId)) {
            throw new IllegalArgumentException("Certification already exists for operator " + command.operatorId()
                    + " and machinery type " + command.machineryTypeId());
        }

        var certification = new MachineryCertification(operator, command.machineryTypeId(), command.expirationDate());
        this.machineryCertificationRepository.save(certification);
        return Optional.of(certification);
    }

    @Override
    public Optional<MachineryCertification> handle(UpdateMachineryCertificationCommand command) {
        if (!this.operatorRepository.existsById(command.operatorId())) {
            throw new IllegalArgumentException("Operator not found with id: " + command.operatorId());
        }

        if (!this.externalMachineryService.existsMachineryTypeById(command.machineryTypeId())) {
            throw new IllegalArgumentException("Machinery type not found with id: " + command.machineryTypeId());
        }

        var certificationId = new MachineryCertificationId(command.operatorId(), command.machineryTypeId());
        var certification = this.machineryCertificationRepository.findById(certificationId)
                .orElseThrow(() -> new IllegalArgumentException("Certification not found for operator "
                        + command.operatorId() + " and machinery type " + command.machineryTypeId()));

        certification.updateExpirationDate(command.expirationDate());
        this.machineryCertificationRepository.save(certification);
        return Optional.of(certification);
    }

    @Override
    public void handle(DeleteMachineryCertificationCommand command) {
        var certificationId = new MachineryCertificationId(command.operatorId(), command.machineryTypeId());
        if (!this.machineryCertificationRepository.existsById(certificationId)) {
            throw new IllegalArgumentException("Certification not found for operator "
                    + command.operatorId() + " and machinery type " + command.machineryTypeId());
        }
        this.machineryCertificationRepository.deleteById(certificationId);
    }
}
