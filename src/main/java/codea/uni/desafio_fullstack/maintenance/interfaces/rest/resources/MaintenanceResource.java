package codea.uni.desafio_fullstack.maintenance.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceResource(
        UUID id,
        String machineryCode,
        LocalDate date,
        float hourMeter,
        UUID operatorId,
        String observation
) {
}
