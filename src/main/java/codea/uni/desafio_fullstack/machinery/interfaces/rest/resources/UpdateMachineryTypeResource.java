package codea.uni.desafio_fullstack.machinery.interfaces.rest.resources;

public record UpdateMachineryTypeResource(Integer id, String name, int maintenanceTime) {
    public UpdateMachineryTypeResource {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Machinery type name cannot be empty");
        }
        if (maintenanceTime <= 0) {
            throw new IllegalArgumentException("Maintenance time must be greater than zero");
        }
    }
}
