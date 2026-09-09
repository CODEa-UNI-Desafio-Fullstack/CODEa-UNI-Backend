package codea.uni.desafio_fullstack.operators.application.internal.commandservices;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.operators.domain.model.commands.UpdateOperatorNameCommand;
import codea.uni.desafio_fullstack.operators.domain.services.OperatorCommandService;
import codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class OperatorCommandServiceImpl implements OperatorCommandService {

    private final OperatorRepository operatorRepository;

    public OperatorCommandServiceImpl(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    public Optional<Operator> handle(CreateOperatorCommand command) {
        var operator = new Operator(command);
        this.operatorRepository.save(operator);
        return Optional.of(operator);
    }

    @Override
    public Optional<Operator> handle(UpdateOperatorNameCommand command) {
        var operator = this.operatorRepository.findById(command.id())
                .orElseThrow(() -> new IllegalArgumentException("Operator not found with id: " + command.id()));

        operator.updateName(command.name());
        this.operatorRepository.save(operator);
        return Optional.of(operator);
    }
}
