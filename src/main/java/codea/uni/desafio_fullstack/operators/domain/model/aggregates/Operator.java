package codea.uni.desafio_fullstack.operators.domain.model.aggregates;

import codea.uni.desafio_fullstack.operators.domain.model.commands.CreateOperatorCommand;
import codea.uni.desafio_fullstack.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "operator")
@Getter
@Setter
@NoArgsConstructor
public class Operator extends AuditableAbstractAggregateRoot<Operator> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification> certifications = new java.util.ArrayList<>();

    public Operator(String name) {
        validateName(name);
        this.name = name.trim();
    }

    public Operator(CreateOperatorCommand command) {
        this(command.name());
    }

    public void updateName(String newName) {
        validateName(newName);
        this.name = newName.trim();
    }

    private void validateName(String nameToValidate) {
        if (nameToValidate == null || nameToValidate.trim().isBlank()) {
            throw new IllegalArgumentException("Operator name cannot be null or empty");
        }
        if (nameToValidate.trim().length() > 50) {
            throw new IllegalArgumentException("Operator name cannot exceed 50 characters");
        }
    }
}
