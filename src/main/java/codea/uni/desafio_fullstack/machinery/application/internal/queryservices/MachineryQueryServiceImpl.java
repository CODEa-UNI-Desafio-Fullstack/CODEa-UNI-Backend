package codea.uni.desafio_fullstack.machinery.application.internal.queryservices;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryByMachineryTypeIdQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryByStateQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetAllMachineryQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByCodeQuery;
import codea.uni.desafio_fullstack.machinery.domain.model.queries.GetMachineryByFilterQuery;
import codea.uni.desafio_fullstack.machinery.domain.services.MachineryQueryService;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryRepository;
import codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories.MachineryTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MachineryQueryServiceImpl implements MachineryQueryService {
    private final MachineryRepository machineryRepository;
    private final MachineryTypeRepository machineryTypeRepository;

    public MachineryQueryServiceImpl(MachineryRepository machineryRepository,MachineryTypeRepository machineryTypeRepository) {
        this.machineryRepository = machineryRepository;
        this.machineryTypeRepository = machineryTypeRepository;
    }

    @Override
    public List<Machinery> handle(GetAllMachineryByMachineryTypeIdQuery query) {

        var machineryType = this.machineryTypeRepository.findById(query.machineryTypeId()).orElse(null);
        return this.machineryRepository.findAllByMachineryType(machineryType);
    }

    @Override
    public List<Machinery> handle(GetAllMachineryByStateQuery query) {
        return this.machineryRepository.findAllByState(query.state());
    }

    @Override
    public Optional<Machinery> handle(GetMachineryByCodeQuery query) {
        return this.machineryRepository.findMachineryByCode(query.code());
    }

    @Override
    public List<Machinery> handle(GetAllMachineryQuery query) {
        return this.machineryRepository.findAll();
    }

    @Override
    public List<Machinery> handle(GetMachineryByFilterQuery query) {
        List<Machinery> machineries = this.machineryRepository.findAll();
        if (machineries.isEmpty()) {
            return List.of();
        }

        return machineries.stream()
                .filter(m -> {
                    if (query.state() != null && !query.state().equals(m.isActive())) {
                        return false;
                    }

                    if (query.machineryTypeId() != null) {
                        if (m.getMachineryType() == null || !query.machineryTypeId().equals(m.getMachineryType().getId())) {
                            return false;
                        }
                    }

                    if (query.code() != null && !query.code().isBlank()) {
                        String term = query.code().trim().toLowerCase();
                        if (m.getCode() == null || !m.getCode().toLowerCase().contains(term)) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();
    }
}
