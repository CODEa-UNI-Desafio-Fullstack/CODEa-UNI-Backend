package codea.uni.desafio_fullstack.maintenance.interfaces.acl.records;

import java.time.LocalDate;
import java.util.UUID;

public record MaintenanceSummaryRecord(
        UUID id,
        String machineryCode,
        LocalDate date,
        float hourMeter,
        UUID operatorId,
        String observation
) {
}
