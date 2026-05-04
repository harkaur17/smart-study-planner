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
import studyPlanner.dto.TaskDTO;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private static final String DEFAULT_COLOR = "#6B4C3B";

    private String randomColor() {
        return DEFAULT_COLOR;
    }

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
        course.setColor(randomColor());
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

    public User getCurrentUserPublic() {
        return getCurrentUser();
    }

    public List<TaskDTO> getTasksForCourse(Long courseId) {
        User user = getCurrentUser();
        Optional<Course> optional = courseRepository.findById(courseId);
        if (!optional.isPresent())
            return null;
        Course course = optional.get();
        if (!course.getUser().getId().equals(user.getId()))
            return null;
        return course.getTasks().stream()
                .map(this::toTaskDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    private TaskDTO toTaskDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.id = task.getId();
        dto.taskName = task.getTaskName();
        dto.taskType = task.getTaskType();
        dto.dueDate = task.getDueDate();
        dto.taskStatus = task.getTaskStatus().name();
        dto.priority = task.getPriority().name();
        dto.courses = task.getCourses().stream()
                .map(c -> new TaskDTO.CourseInfo(c.getId(), c.getCode(), c.getName()))
                .collect(java.util.stream.Collectors.toList());
        return dto;
    }

    public Course getCourse(Long id) {
        User user = getCurrentUser();
        Optional<Course> optional = courseRepository.findById(id);
        if (!optional.isPresent())
            return null;
        Course course = optional.get();
        if (!course.getUser().getId().equals(user.getId()))
            return null;
        return course;
    }
}