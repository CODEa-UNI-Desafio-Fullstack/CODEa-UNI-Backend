package codea.uni.desafio_fullstack.operators.interfaces.rest.transform;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.interfaces.rest.resources.OperatorResource;

public class OperatorResourceFromEntityAssembler {
    public static OperatorResource toResourceFromEntity(Operator entity) {
        return new OperatorResource(entity.getId(), entity.getName());
    }
}
