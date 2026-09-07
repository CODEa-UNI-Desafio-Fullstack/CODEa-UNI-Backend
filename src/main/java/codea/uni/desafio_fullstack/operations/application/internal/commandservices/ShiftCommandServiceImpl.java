package codea.uni.desafio_fullstack.operations.application.internal.commandservices;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.DeleteShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.model.commands.UpdateShiftCommand;
import codea.uni.desafio_fullstack.operations.domain.services.ShiftCommandService;
import codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories.ShiftRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ShiftCommandServiceImpl implements ShiftCommandService {

    private final ShiftRepository shiftRepository;

    public ShiftCommandServiceImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public Optional<Shift> handle(CreateShiftCommand command) {
        var shift = new Shift(command);
        var savedShift = this.shiftRepository.save(shift);
        return Optional.of(savedShift);
    }

    @Override
    public Optional<Shift> handle(UpdateShiftCommand command) {
        var shiftOptional = this.shiftRepository.findById(command.id());
        if (shiftOptional.isEmpty()) {
            throw new IllegalArgumentException("Shift with id " + command.id() + " does not exist");
        }

        var shift = shiftOptional.get();
        shift.update(command.date(), command.shiftType(), command.duration());
        var updatedShift = this.shiftRepository.save(shift);
        return Optional.of(updatedShift);
    }

    @Override
    public void handle(DeleteShiftCommand command) {
        var shiftOptional = this.shiftRepository.findById(command.id());
        if (shiftOptional.isEmpty()) {
            throw new IllegalArgumentException("Shift with id " + command.id() + " does not exist");
        }
        this.shiftRepository.delete(shiftOptional.get());
    }
}
