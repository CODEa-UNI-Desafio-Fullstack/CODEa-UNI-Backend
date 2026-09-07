package codea.uni.desafio_fullstack.operations.domain.model.aggregates;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateShiftCommand;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "shifts", indexes = {
        @Index(name = "idx_shifts_date", columnList = "date"),
        @Index(name = "idx_shifts_shift_type", columnList = "shift_type")
})
@Getter
@Setter
@NoArgsConstructor
public class Shift extends AuditableAbstractAggregateRoot<Shift> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "shift_type", nullable = false)
    private Boolean shiftType;

    @Column(nullable = false)
    private Integer duration;

    public Shift(CreateShiftCommand command) {
        validateDate(command.date());
        validateShiftType(command.shiftType());
        validateDuration(command.duration());

        this.date = command.date();
        this.shiftType = command.shiftType();
        this.duration = command.duration();
    }

    public void update(LocalDate date, Boolean shiftType, Integer duration) {
        validateDate(date);
        validateShiftType(shiftType);
        validateDuration(duration);

        this.date = date;
        this.shiftType = shiftType;
        this.duration = duration;
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Shift date cannot be null");
        }
    }

    private void validateShiftType(Boolean shiftType) {
        if (shiftType == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }
    }

    private void validateDuration(Integer duration) {
        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Shift duration must be greater than zero");
        }
    }
}
