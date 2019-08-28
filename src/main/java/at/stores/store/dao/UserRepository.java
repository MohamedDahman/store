package at.stores.store.dao;

import at.stores.store.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    Optional<Users> findOneByLogin(String loing);
    Optional<Users> findOneByEmail(String email);

}
