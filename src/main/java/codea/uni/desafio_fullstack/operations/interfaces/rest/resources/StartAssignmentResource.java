package codea.uni.desafio_fullstack.operations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record StartAssignmentResource(
        @Schema(type = "string", example = "19:00", description = "Hora de inicio del turno en formato HH:mm (ej: '19:00'). Opcional, por defecto hora actual")
        LocalTime timeStart
) {
}
