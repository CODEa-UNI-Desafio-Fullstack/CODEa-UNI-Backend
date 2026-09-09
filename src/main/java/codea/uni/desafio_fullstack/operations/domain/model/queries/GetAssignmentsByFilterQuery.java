package codea.uni.desafio_fullstack.operations.domain.model.queries;

import java.time.LocalDate;

public record GetAssignmentsByFilterQuery(
        String operatorName,
        String machineryType,
        String machineryCode,
        LocalDate startDate,
        LocalDate endDate,
        Boolean shiftType
) {
}
