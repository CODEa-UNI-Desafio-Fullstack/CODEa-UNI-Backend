package codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.machinery.domain.model.aggregates.Machinery;
import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MachineryRepository extends JpaRepository <Machinery, String> {
    List<Machinery> findAll();
    Optional<Machinery> findMachineryByCode(String machineryCode);
    List<Machinery> findAllByState(boolean state);

    List<Machinery> findAllByMachineryType(MachineryType machineryType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Machinery m JOIN FETCH m.machineryType WHERE m.code = :code")
    Optional<Machinery> findByCodeWithLock(@Param("code") String code);

    @Query("SELECT m FROM Machinery m JOIN FETCH m.machineryType WHERE m.code = :code")
    Optional<Machinery> findByCodeWithType(@Param("code") String code);
}

