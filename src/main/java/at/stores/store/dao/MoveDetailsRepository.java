package at.stores.store.dao;

import at.stores.store.entity.MoveDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoveDetailsRepository extends JpaRepository<MoveDetails,Long> {
}
