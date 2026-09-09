package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record EndAssignmentResource(
        @Schema(type = "string", example = "05:00", description = "Hora de fin del turno en formato HH:mm (ej: '05:00'). Opcional, por defecto hora actual")
        LocalTime timeEnd
) {
}
