package codea.uni.desafio_fullstack.maintenance.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.maintenance.domain.model.aggregates.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, UUID> {
    List<Maintenance> findAllByMachineryCode(String machineryCode);
    List<Maintenance> findAllByOperatorId(UUID operatorId);
    List<Maintenance> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
}
