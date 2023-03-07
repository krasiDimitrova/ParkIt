package uni.fmi.parkit.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uni.fmi.parkit.server.models.ParkItUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<ParkItUser, Long> {

    boolean existsByEmail(String email);

    Optional<ParkItUser> findByEmail(String email);
}
