package codea.uni.desafio_fullstack.operations.application.internal.queryservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetAllShiftsQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftByIdQuery;
import codea.uni.desafio_fullstack.operations.domain.model.queries.GetShiftsByFilterQuery;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftQueryService;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ShiftQueryServiceImpl implements ShiftQueryService {

    private final ShiftRepository shiftRepository;

    public ShiftQueryServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public List<Shift> handle(GetAllShiftsQuery query) {
        return this.shiftRepository.findAll();
    }

    @Override
    public Optional<Shift> handle(GetShiftByIdQuery query) {
        return this.shiftRepository.findById(query.id());
    }

    @Override
    public List<Shift> handle(GetShiftsByFilterQuery query) {
        if (query.date() != null && query.shiftType() != null) {
            return this.shiftRepository.findAllByDateAndShiftType(query.date(), query.shiftType());
        } else if (query.date() != null) {
            return this.shiftRepository.findAllByDate(query.date());
        } else if (query.shiftType() != null) {
            return this.shiftRepository.findAllByShiftType(query.shiftType());
        } else {
            return this.shiftRepository.findAll();
        }
    }
}
