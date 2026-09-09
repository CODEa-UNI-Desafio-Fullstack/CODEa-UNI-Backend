package codea.uni.desafio_fullstack.maintenance.domain.model.queries;

public record GetMaintenancesByMachineryCodeQuery(String machineryCode) {
    public GetMaintenancesByMachineryCodeQuery {
        if (machineryCode == null || machineryCode.isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or blank");
        }
    }
}
