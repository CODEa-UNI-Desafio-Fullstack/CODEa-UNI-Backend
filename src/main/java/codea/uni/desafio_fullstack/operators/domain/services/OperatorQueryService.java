package codea.uni.desafio_fullstack.operators.domain.services;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllOperatorsQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorByIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByFilterQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetOperatorsByMachineryTypeCertificationQuery;

import java.util.List;
import java.util.Optional;

public interface OperatorQueryService {
    Optional<Operator> handle(GetOperatorByIdQuery query);
    List<Operator> handle(GetAllOperatorsQuery query);
    List<Operator> handle(GetOperatorsByMachineryTypeCertificationQuery query);
    List<Operator> handle(GetOperatorsByFilterQuery query);
}
