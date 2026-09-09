package codea.uni.desafio_fullstack.maintenance.domain.model.aggregates;

import codea.uni.desafio_fullstack.maintenance.domain.model.commands.CreateMaintenanceCommand;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "maintenance", indexes = {
        @Index(name = "idx_maintenance_machinery_code", columnList = "machinery_code"),
        @Index(name = "idx_maintenance_operator_id", columnList = "operator_id"),
        @Index(name = "idx_maintenance_date", columnList = "date")
})
@Getter
@Setter
@NoArgsConstructor
public class Maintenance extends AuditableAbstractAggregateRoot<Maintenance> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(name = "machinery_code", nullable = false, length = 20)
    private String machineryCode;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "hour_meter", nullable = false)
    private float hourMeter;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(columnDefinition = "TEXT")
    private String observation;

    public Maintenance(CreateMaintenanceCommand command) {
        validateMachineryCode(command.machineryCode());
        validateDate(command.date());
        validateHourMeter(command.hourMeter());
        validateOperatorId(command.operatorId());

        this.machineryCode = command.machineryCode().trim();
        this.date = command.date();
        this.hourMeter = command.hourMeter();
        this.operatorId = command.operatorId();
        this.observation = command.observation() != null ? command.observation().trim() : null;
    }

    public void updateDetails(float hourMeter, UUID operatorId, String observation) {
        validateHourMeter(hourMeter);
        validateOperatorId(operatorId);

        this.hourMeter = hourMeter;
        this.operatorId = operatorId;
        this.observation = observation != null ? observation.trim() : null;
    }

    private void validateMachineryCode(String code) {
        if (code == null || code.trim().isBlank()) {
            throw new IllegalArgumentException("Machinery code cannot be null or blank");
        }
        if (code.trim().length() > 20) {
            throw new IllegalArgumentException("Machinery code cannot exceed 20 characters");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Maintenance date cannot be null");
        }
    }

    private void validateHourMeter(float hours) {
        if (hours < 0) {
            throw new IllegalArgumentException("Hour meter cannot be negative");
        }
    }

    private void validateOperatorId(UUID operatorId) {
        if (operatorId == null) {
            throw new IllegalArgumentException("Operator ID cannot be null");
        }
    }
}
