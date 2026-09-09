package codea.uni.desafio_fullstack.machinery.domain.model.entities;

import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="machinery_type")
@Getter
@Setter
@NoArgsConstructor
public class MachineryType extends AuditableAbstractAggregateRoot<MachineryType> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true, length = 60)
    private String name;

    @Column(nullable = false, name = "maintenance_time")
    private int maintenanceTime;

    public MachineryType(codea.uni.desafio_fullstack.machinery.domain.model.commands.CreateMachineryTypeCommand command) {
        this.name = command.name();
        this.maintenanceTime = command.maintenanceTime();
    }
}
