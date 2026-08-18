package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.StudySession;
import studyPlanner.model.User;
import java.util.List;
import java.util.Optional;

public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    Optional<StudySession> findFirstByUserAndEndedAtIsNull(User user);
    List<StudySession> findByUserOrderByStartedAtDesc(User user);
}