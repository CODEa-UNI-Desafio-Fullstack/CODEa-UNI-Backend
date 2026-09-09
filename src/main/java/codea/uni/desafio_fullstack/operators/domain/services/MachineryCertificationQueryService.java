package codea.uni.desafio_fullstack.operators.domain.services;

import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetAllCertificationsByOperatorIdQuery;
import codea.uni.desafio_fullstack.operators.domain.model.queries.GetCertificationByOperatorIdAndMachineryTypeIdQuery;

import java.util.List;
import java.util.Optional;

public interface MachineryCertificationQueryService {
    List<MachineryCertification> handle(GetAllCertificationsByOperatorIdQuery query);
    Optional<MachineryCertification> handle(GetCertificationByOperatorIdAndMachineryTypeIdQuery query);
}
