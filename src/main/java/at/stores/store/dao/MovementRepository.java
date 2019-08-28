package at.stores.store.dao;

import at.stores.store.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement , Long> {

    List<Movement> findAllByMoveDateBetween(LocalDate localDateStart,LocalDate localDateEnd);

}
