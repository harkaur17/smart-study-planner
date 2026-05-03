package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.model.ActivityLog;
import studyPlanner.model.Course;
import studyPlanner.model.Task;
import studyPlanner.model.User;
import studyPlanner.repository.ActivityLogRepository;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    // get current logged in user from JWT token
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // get all courses for current user
    public List<Course> getAllCourses() {
        User user = getCurrentUser();
        return courseRepository.findByUser(user);
    }

    // add a course
    public Course addCourse(String name, String code) {
        User user = getCurrentUser();
        Optional<Course> existing = courseRepository.findByCodeAndUser(code, user);
        if (existing.isPresent())
            return null;
        Course course = new Course(name, code, user);
        Course saved = courseRepository.save(course);
        activityLogRepository.save(new ActivityLog(
                user, ActivityLog.ActionType.COURSE_ADDED,
                "Added course " + code, 5));
        return saved;
    }

    // delete course
    public boolean deleteCourse(Long id) {
        User user = getCurrentUser();
        Optional<Course> optional = courseRepository.findById(id);
        if (!optional.isPresent())
            return false;
        Course course = optional.get();
        if (!course.getUser().getId().equals(user.getId()))
            return false;
        for (Task task : course.getTasks()) {
            task.removeCourse(course);
        }
        courseRepository.delete(course);
        return true;
    }

    // edit a course
    public Course editCourse(Long id, String newName, String newCode) {
        User user = getCurrentUser();
        Optional<Course> optional = courseRepository.findById(id);
        if (!optional.isPresent())
            return null;
        Course course = optional.get();
        if (!course.getUser().getId().equals(user.getId()))
            return null;
        if (newName != null && !newName.trim().isEmpty()) {
            course.setName(newName);
        }
        if (newCode != null && !newCode.trim().isEmpty()) {
            course.setCode(newCode);
        }
        return courseRepository.save(course);
    }
}