package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.StudyBuddyConnection;
import studyPlanner.model.User;
import java.util.List;
import java.util.Optional;

public interface StudyBuddyConnectionRepository extends JpaRepository<StudyBuddyConnection, Long> {
    Optional<StudyBuddyConnection> findByFollowerAndFollowing(User follower, User following);

    List<StudyBuddyConnection> findByFollowerAndStatus(User follower, StudyBuddyConnection.Status status);

    List<StudyBuddyConnection> findByFollowingAndStatus(User following, StudyBuddyConnection.Status status);

    long countByFollowerAndStatus(User follower, StudyBuddyConnection.Status status);

    long countByFollowingAndStatus(User following, StudyBuddyConnection.Status status);
}
