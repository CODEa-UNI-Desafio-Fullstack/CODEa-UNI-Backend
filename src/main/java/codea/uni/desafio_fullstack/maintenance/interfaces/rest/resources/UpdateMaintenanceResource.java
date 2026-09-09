package codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources;

import java.util.UUID;

public record UpdateMaintenanceResource(
        float hourMeter,
        UUID operatorId,
        String observation
) {
}
