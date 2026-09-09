package codea.uni.desafio_fullstack.operators.domain.services;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateOperatorNameCommand;

import java.util.Optional;

public interface OperatorCommandService {
    Optional<Operator> handle(CreateOperatorCommand command);
    Optional<Operator> handle(UpdateOperatorNameCommand command);
}
