package codea.uni.desafio_fullstack.machinery.infrastructure.persistence.jpa.repositories;

import codea.uni.desafio_fullstack.machinery.domain.model.entities.MachineryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MachineryTypeRepository extends JpaRepository<MachineryType, Integer> {
}
