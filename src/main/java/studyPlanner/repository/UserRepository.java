package studyPlanner.repository;

import studyPlanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    //Optional -> this might return a user, or null
    Optional<User> findByEmail(String email);    
}