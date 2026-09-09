package codea.uni.desafio_fullstack.operators.interfaces.acl.records;

import java.time.LocalDate;
import java.util.UUID;

public record OperatorCertificationSummaryRecord(UUID operatorId, Integer machineryTypeId, LocalDate expirationDate, boolean valid) {
}
