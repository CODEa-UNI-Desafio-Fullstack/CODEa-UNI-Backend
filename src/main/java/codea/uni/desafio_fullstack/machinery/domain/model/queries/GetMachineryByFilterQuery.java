package codea.uni.desafio_fullstack.machinery.domain.model.queries;

public record GetMachineryByFilterQuery(
        Boolean state,
        Integer machineryTypeId,
        String code
) {
    public GetMachineryByFilterQuery(Boolean state, Integer machineryTypeId) {
        this(state, machineryTypeId, null);
    }
}
