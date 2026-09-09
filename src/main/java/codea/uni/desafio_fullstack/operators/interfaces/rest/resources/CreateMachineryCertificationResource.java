package codea.uni.desafio_fullstack.operators.interfaces.rest.resources;

import java.time.LocalDate;

public record CreateMachineryCertificationResource(Integer machineryTypeId, LocalDate expirationDate) {
}
