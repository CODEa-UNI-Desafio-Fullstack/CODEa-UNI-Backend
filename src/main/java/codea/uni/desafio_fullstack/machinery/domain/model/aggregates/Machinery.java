package codea.uni.desafio_fullstack.machinery.domain.model.aggregates;

import codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryCommand;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="machinery")
@Setter
@Getter
@NoArgsConstructor
public class Machinery extends AuditableAbstractAggregateRoot<Machinery> {
    @Id
    @Column(nullable = false,unique = true, length = 20)
    private String code;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private MachineryType machineryType;

    @Column(nullable = false, name="hour_meter")
    private float hourMeter = 0.0f;

    @Column(nullable = false)
    private boolean state = true; // true: ACTIVO, false: BLOQUEADO

    public Machinery(CreateMachineryCommand command, MachineryType machineryType) {
        this.code = command.code();
        this.machineryType = machineryType;
    }

//    Records worked hours from an operation or closed shift.
//    Increments the hour meter and automatically blocks the equipment if it reaches or exceeds the maintenance threshold.
//    @param workedHours Hours worked during the shift (must be positive)
//    @return true if the equipment is now blocked, false otherwise
    public boolean recordWorkedHours(float workedHours) {
        if (workedHours <= 0) {
            throw new IllegalArgumentException("Worked hours must be greater than zero");
        }
        this.hourMeter += workedHours;
        return checkAndApplyMaintenanceThreshold();
    }


//   Checks if current hour meter has reached or exceeded the maintenance threshold of its machinery type.
//   If so, marks the state as false (BLOQUEADO).
//
//   @return true if the machinery is blocked, false if active
    public boolean checkAndApplyMaintenanceThreshold() {
        if (this.machineryType != null && this.hourMeter >= this.machineryType.getMaintenanceTime()) {
            this.state = false;
        }
        return isBlocked();
    }


//    Resets the hour meter and unblocks the machinery following completed maintenance.
//    Implements Business Policy P3 (POLITICA_REINICIO_CICLO_MANTENIMIENTO = horometer resets to 0 upon maintenance).
    public void resetAfterMaintenance() {
        this.hourMeter = 0.0f;
        this.state = true;
    }

    public void blockForMaintenance() {
        this.state = false;
    }

    public void unblock() {
        this.state = true;
    }

    public boolean isBlocked() {
        return !this.state;
    }

    public boolean isActive() {
        return this.state;
    }

    public float getRemainingHoursToMaintenance() {
        if (this.machineryType == null) {
            return 0.0f;
        }
        return Math.max(0.0f, this.machineryType.getMaintenanceTime() - this.hourMeter);
    }

    public boolean willExceedMaintenance(float additionalHours) {
        if (this.machineryType == null) {
            return false;
        }
        return (this.hourMeter + additionalHours) >= this.machineryType.getMaintenanceTime();
    }
}

