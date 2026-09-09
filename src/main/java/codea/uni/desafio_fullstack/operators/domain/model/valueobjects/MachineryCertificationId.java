package codea.uni.desafio_fullstack.operators.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MachineryCertificationId implements Serializable {

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(name = "machinery_type", nullable = false)
    private Integer machineryType;
}
