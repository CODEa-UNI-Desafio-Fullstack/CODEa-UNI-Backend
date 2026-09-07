package codea.uni.desafio_fullstack.operators.application.internal.queryservices;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllOperatorsQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByMachineryTypeCertificationQuery;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorQueryService;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OperatorQueryServiceImpl implements OperatorQueryService {

    private final OperatorRepository operatorRepository;

    public OperatorQueryServiceImpl(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    public Optional<Operator> handle(GetOperatorByIdQuery query) {
        return this.operatorRepository.findById(query.id());
    }

    @Override
    public List<Operator> handle(GetAllOperatorsQuery query) {
        return this.operatorRepository.findAll();
    }

    @Override
    public List<Operator> handle(GetOperatorsByMachineryTypeCertificationQuery query) {
        return this.operatorRepository.findDistinctByCertificationsIdMachineryType(query.machineryTypeId());
    }

    @Override
    public List<Operator> handle(GetOperatorsByFilterQuery query) {
        List<Operator> operators;
        if (query.machineryTypeId() != null) {
            operators = this.operatorRepository.findDistinctByCertificationsIdMachineryType(query.machineryTypeId());
        } else {
            operators = this.operatorRepository.findAll();
        }

        if (query.name() != null && !query.name().isBlank()) {
            String term = query.name().trim().toLowerCase();
            operators = operators.stream()
                    .filter(o -> o.getName() != null && o.getName().toLowerCase().contains(term))
                    .toList();
        }

        return operators;
    }
}

