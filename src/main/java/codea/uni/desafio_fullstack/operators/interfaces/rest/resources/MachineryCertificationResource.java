package codea.uni.desafio_fullstack.operators.interfaces.rest.resources;

import java.time.LocalDate;
import java.util.UUID;

public record MachineryCertificationResource(UUID operatorId, Integer machineryTypeId, LocalDate expirationDate) {
}
