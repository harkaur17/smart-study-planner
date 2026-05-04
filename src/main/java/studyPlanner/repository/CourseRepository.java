package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.Course;
import studyPlanner.model.User;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByUser(User user);

    Optional<Course> findByCodeAndUser(String code, User user);

    List<Course> findByUserAndIsCompleted(User user, boolean isCompleted);
}