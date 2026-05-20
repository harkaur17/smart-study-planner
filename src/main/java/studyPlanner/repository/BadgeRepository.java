package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.Badge;
import studyPlanner.model.User;
import java.util.List;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
    List<Badge> findByUser(User user);

    boolean existsByUserAndBadgeType(User user, Badge.BadgeType badgeType);
}
