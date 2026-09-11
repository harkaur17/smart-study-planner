package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.JoinRequest;
import studyPlanner.model.StudySession;
import studyPlanner.model.User;
import java.util.List;
import java.util.Optional;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    Optional<JoinRequest> findByRequesterAndSession(User requester, StudySession session);

    List<JoinRequest> findBySessionAndStatus(StudySession session, JoinRequest.Status status);

    void deleteBySession(StudySession session);
}
