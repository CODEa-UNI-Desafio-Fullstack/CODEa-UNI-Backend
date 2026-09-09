package codea.uni.desafio_fullstack.operators.application.internal.queryservices;

import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllCertificationsByOperatorIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.operators.domain.services.MachineryCertificationQueryService;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.MachineryCertificationRepository;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MachineryCertificationQueryServiceImpl implements MachineryCertificationQueryService {

    private final MachineryCertificationRepository machineryCertificationRepository;
    private final OperatorRepository operatorRepository;

    public MachineryCertificationQueryServiceImpl(MachineryCertificationRepository machineryCertificationRepository,
                                                 OperatorRepository operatorRepository) {
        this.machineryCertificationRepository = machineryCertificationRepository;
        this.operatorRepository = operatorRepository;
    }

    @Override
    public List<MachineryCertification> handle(GetAllCertificationsByOperatorIdQuery query) {
        if (!this.operatorRepository.existsById(query.operatorId())) {
            throw new IllegalArgumentException("Operator not found with id: " + query.operatorId());
        }
        return this.machineryCertificationRepository.findAllByIdOperatorId(query.operatorId());
    }

    @Override
    public Optional<MachineryCertification> handle(GetCertificationByOperatorIdAndMachineryTypeIdQuery query) {
        return this.machineryCertificationRepository.findByIdOperatorIdAndIdMachineryType(
                query.operatorId(),
                query.machineryTypeId()
        );
    }
}
