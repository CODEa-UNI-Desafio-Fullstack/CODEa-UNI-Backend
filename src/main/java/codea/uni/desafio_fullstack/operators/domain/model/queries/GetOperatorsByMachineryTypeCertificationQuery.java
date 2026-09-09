package codea.uni.desafio_fullstack.operators.domain.model.queries;

public record GetOperatorsByMachineryTypeCertificationQuery(Integer machineryTypeId) {
    public GetOperatorsByMachineryTypeCertificationQuery {
        if (machineryTypeId == null || machineryTypeId <= 0) {
            throw new IllegalArgumentException("Machinery type ID must be a positive integer");
        }
    }
}
