package codea.uni.desafio_fullstack.machinery.domain.services;

import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryTypesQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryTypeByIdQuery;

import java.util.List;
import java.util.Optional;

public interface MachineryTypeQueryService {
    Optional<MachineryType> handle(GetMachineryTypeByIdQuery query);
    List<MachineryType> handle(GetAllMachineryTypesQuery query);
}
