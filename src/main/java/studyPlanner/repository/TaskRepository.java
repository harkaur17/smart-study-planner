package studyPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import studyPlanner.model.Task;
import studyPlanner.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);

    List<Task> findByUserOrderByDueDateAsc(User user);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = studyPlanner.model.Task$Status.DONE")
    long countDoneTasksByUser(@Param("user") User user);
}