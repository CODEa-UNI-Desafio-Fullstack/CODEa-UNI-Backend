package codea.uni.desafio_fullstack.operators.domain.model.entities;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import codea.uni.desafio_fullstack.operators.domain.model.valueobjects.MachineryCertificationId;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "machinery_certification")
@Getter
@Setter
@NoArgsConstructor
public class MachineryCertification extends AuditableAbstractAggregateRoot<MachineryCertification> {

    @EmbeddedId
    private MachineryCertificationId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("operatorId")
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    public MachineryCertification(Operator operator, Integer machineryTypeId, LocalDate expirationDate) {
        if (operator == null) {
            throw new IllegalArgumentException("Operator cannot be null");
        }
        if (machineryTypeId == null || machineryTypeId <= 0) {
            throw new IllegalArgumentException("Machinery type ID must be a positive integer");
        }
        validateExpirationDate(expirationDate);

        this.operator = operator;
        this.id = new MachineryCertificationId(operator.getId(), machineryTypeId);
        this.expirationDate = expirationDate;
    }

    public void updateExpirationDate(LocalDate newExpirationDate) {
        validateExpirationDate(newExpirationDate);
        this.expirationDate = newExpirationDate;
    }

    public boolean isExpiredOn(LocalDate date) {
        if (this.expirationDate == null || date == null) {
            return true;
        }
        return this.expirationDate.isBefore(date);
    }

    public boolean isValidOn(LocalDate date) {
        return !isExpiredOn(date);
    }

    private void validateExpirationDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Expiration date cannot be null");
        }
    }
}
