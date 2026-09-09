package codea.uni.desafio_fullstack.machinery.interfaces.rest.resources;

public record CreateMachineryTypeResource(String name, int maintenanceTime) {
    public CreateMachineryTypeResource {
        if(name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Machinery type name cannot be empty");
        }
        if(maintenanceTime < 0){
            throw new IllegalArgumentException("Maintenance time cannot be negative");
        }
    }
}
