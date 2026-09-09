package codea.uni.desafio_fullstack.operations.domain.model.queries;

public record GetMachineryMaintenanceProjectionsQuery(
        String machineryCode,
        String machineryType
) {
}
