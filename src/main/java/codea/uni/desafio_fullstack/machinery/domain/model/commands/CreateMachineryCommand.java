package codea.uni.desafio_fullstack.machinery.domain.model.commands;

import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;

public record CreateMachineryCommand(String code, Integer machineryTypeId) {
}
