package codea.uni.desafio_fullstack.operators.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.operators.domain.model.aggregates.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, UUID> {

    @Query("SELECT DISTINCT o FROM Operator o JOIN o.certifications c WHERE c.id.machineryType = :machineryTypeId")
    List<Operator> findDistinctByCertificationsIdMachineryType(@Param("machineryTypeId") Integer machineryTypeId);
}
