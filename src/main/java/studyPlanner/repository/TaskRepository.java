package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.Task;
import studyPlanner.model.User;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    List<Task> findByUserOrderByDueDateAsc(User user);
}