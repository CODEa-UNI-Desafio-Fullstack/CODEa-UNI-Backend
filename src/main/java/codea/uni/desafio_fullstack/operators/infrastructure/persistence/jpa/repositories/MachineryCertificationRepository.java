package codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.operators.domain.model.entities.MachineryCertification;
import codea.uni.desafio_fullstack.operators.domain.model.valueobjects.MachineryCertificationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MachineryCertificationRepository extends JpaRepository<MachineryCertification, MachineryCertificationId> {
    List<MachineryCertification> findAllByIdOperatorId(UUID operatorId);
    Optional<MachineryCertification> findByIdOperatorIdAndIdMachineryType(UUID operatorId, Integer machineryType);
    boolean existsByIdOperatorIdAndIdMachineryType(UUID operatorId, Integer machineryType);
    void deleteByIdOperatorIdAndIdMachineryType(UUID operatorId, Integer machineryType);
}
