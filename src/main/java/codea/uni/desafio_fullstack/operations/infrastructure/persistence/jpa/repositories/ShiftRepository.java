package codea.uni.desafio_fullstack.operations.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.operations.domain.model.aggregates.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findAllByDate(LocalDate date);
    List<Shift> findAllByShiftType(Boolean shiftType);
    List<Shift> findAllByDateAndShiftType(LocalDate date, Boolean shiftType);
}
