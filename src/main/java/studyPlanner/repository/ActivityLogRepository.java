package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.ActivityLog;
import studyPlanner.model.User;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByUserOrderByCreatedAtDesc(User user);

    List<ActivityLog> findTop10ByUserOrderByCreatedAtDesc(User user);
}