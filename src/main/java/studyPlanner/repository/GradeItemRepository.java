package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.GradeItem;
import studyPlanner.model.Course;
import studyPlanner.model.User;
import java.util.List;

public interface GradeItemRepository extends JpaRepository<GradeItem, Long> {
    List<GradeItem> findByCourseAndUser(Course course, User user);
}
