package codea.uni.desafio_fullstack.operations.domain.model.aggregates;

import codea.uni.desafio_fullstack.operations.domain.model.commands.CreateAssignmentCommand;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assignments", indexes = {
        @Index(name = "idx_assignment_shift_id", columnList = "shift_id"),
        @Index(name = "idx_assignment_operator_id", columnList = "operator_id"),
        @Index(name = "idx_assignment_machinery_code", columnList = "machinery_code")
})
@Getter
@Setter
@NoArgsConstructor
public class Assignment extends AuditableAbstractAggregateRoot<Assignment> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(name = "machinery_code", nullable = false, length = 20)
    private String machineryCode;

    @Column(name = "shift_id", nullable = false)
    private UUID shiftId;

    @Column(name = "time_start")
    private LocalDateTime timeStart;

    @Column(name = "time_end")
    private LocalDateTime timeEnd;

    @Column(name = "actual_shift_time")
    private Integer actualShiftTime;

    public Assignment(CreateAssignmentCommand command) {
        validateOperatorId(command.operatorId());
        validateMachineryCode(command.machineryCode());
        validateShiftId(command.shiftId());

        this.operatorId = command.operatorId();
        this.machineryCode = command.machineryCode().trim();
        this.shiftId = command.shiftId();
    }

    public void updateDetails(UUID operatorId, String machineryCode, UUID shiftId) {
        validateOperatorId(operatorId);
        validateMachineryCode(machineryCode);
        validateShiftId(shiftId);

        this.operatorId = operatorId;
        this.machineryCode = machineryCode.trim();
        this.shiftId = shiftId;
    }

    public void startShift(LocalDateTime timeStart) {
        this.timeStart = (timeStart != null) ? timeStart : LocalDateTime.now();
    }

    public void endShift(LocalDateTime timeEnd) {
        if (this.timeStart == null) {
            throw new IllegalStateException("Cannot end an assignment that has not started yet");
        }

        LocalDateTime end = (timeEnd != null) ? timeEnd : LocalDateTime.now();
        if (end.isBefore(this.timeStart)) {
            throw new IllegalArgumentException("Shift end time cannot be before start time");
        }

        this.timeEnd = end;
        long minutes = Duration.between(this.timeStart, this.timeEnd).toMinutes();
        this.actualShiftTime = (int) Math.round((double) minutes / 60.0);
    }

    private void validateOperatorId(UUID operatorId) {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }

    private void validateMachineryCode(String code) {
        if (code == null || code.trim().isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or blank");
        }
        if (code.trim().length() > 20) {
            throw new IllegalArgumentException("Machinery code cannot exceed 20 characters");
        }
    }

    private void validateShiftId(UUID shiftId) {
        if (shiftId == null) {
            throw new IllegalArgumentException("Shift ID cannot be null");
        }
    }
}
