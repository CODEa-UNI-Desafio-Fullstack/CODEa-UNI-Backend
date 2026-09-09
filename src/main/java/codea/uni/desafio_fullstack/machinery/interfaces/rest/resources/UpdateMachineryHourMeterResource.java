package codea.uni.desafio_fullstack.machinery.interfaces.rest.resources;

public record UpdateMachineryHourMeterResource(String code, float hours) {
    public UpdateMachineryHourMeterResource {
        if (hours < 0) {
            throw new IllegalArgumentException("Hours must be non-negative");
        }
    }
}
