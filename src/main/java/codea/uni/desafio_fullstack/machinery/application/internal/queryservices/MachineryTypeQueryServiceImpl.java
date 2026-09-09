package codea.uni.desafio_fullstack.machinery.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryTypesQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryTypeByIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryTypeQueryService;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineryTypeQueryServiceImpl implements MachineryTypeQueryService {
    private final MachineryTypeRepository machineryTypeRepository;

    public MachineryTypeQueryServiceImpl(MachineryTypeRepository machineryTypeRepository) {
        this.machineryTypeRepository = machineryTypeRepository;
    }

    @Override
    public Optional<MachineryType> handle(GetMachineryTypeByIdQuery query) {
        return this.machineryTypeRepository.findById(query.id());
    }

    @Override
    public List<MachineryType> handle(GetAllMachineryTypesQuery query) {
        return this.machineryTypeRepository.findAll();
    }
}
