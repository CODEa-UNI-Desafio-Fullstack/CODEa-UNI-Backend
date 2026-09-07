package codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findAllByShiftId(UUID shiftId);
    List<Assignment> findAllByOperatorId(UUID operatorId);
    List<Assignment> findAllByMachineryCode(String machineryCode);
}
