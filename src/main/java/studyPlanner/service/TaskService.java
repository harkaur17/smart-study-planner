package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.dto.TaskDTO;
import studyPlanner.model.ActivityLog;
import studyPlanner.model.Course;
import studyPlanner.model.Task;
import studyPlanner.model.User;
import studyPlanner.repository.ActivityLogRepository;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.TaskRepository;
import studyPlanner.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

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

    // get all tasks for current user
    public List<TaskDTO> getAllTasks() {
        User user = getCurrentUser();
        return taskRepository.findByUserOrderByDueDateAsc(user)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // add a task
    public TaskDTO addTask(String name, String type, String dueDateStr,
            String status, String priority, List<String> courseCodes) {
        try {
            User user = getCurrentUser();
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());

            LocalDate dueDate = null;
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                dueDate = LocalDate.parse(dueDateStr);
            }

            Task task;
            if (dueDate != null) {
                task = new Task(name, type, dueDate, taskStatus, taskPriority, user);
            } else {
                task = new Task(name, type, taskStatus, taskPriority, user);
            }

            // link courses
            if (courseCodes != null) {
                for (String code : courseCodes) {
                    Optional<Course> course = courseRepository.findByCodeAndUser(code, user);
                    if (course.isPresent()) {
                        task.addCourse(course.get());
                    }
                }
            }

            Task saved = taskRepository.save(task);
            activityLogRepository.save(new ActivityLog(
                    user, ActivityLog.ActionType.TASK_ADDED,
                    "Added task: " + name, 5));
            return toDTO(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // delete a task
    public boolean deleteTask(Long id) {
        User user = getCurrentUser();
        Optional<Task> optional = taskRepository.findById(id);
        if (!optional.isPresent())
            return false;
        Task task = optional.get();
        if (!task.getUser().getId().equals(user.getId()))
            return false;
        ArrayList<Course> linkedCourses = new ArrayList<>(task.getCourses());
        for (Course course : linkedCourses) {
            task.removeCourse(course);
        }
        taskRepository.delete(task);
        return true;
    }

    // edit a task
    public TaskDTO editTask(Long id, String newName, String newType, String newDueDate,
            String newStatus, String newPriority, List<String> newCourseCodes) {
        User user = getCurrentUser();
        Optional<Task> optional = taskRepository.findById(id);
        if (!optional.isPresent())
            return null;
        Task task = optional.get();
        if (!task.getUser().getId().equals(user.getId()))
            return null;

        try {
            if (newName != null && !newName.trim().isEmpty())
                task.setName(newName);
            if (newType != null && !newType.trim().isEmpty())
                task.setTaskType(newType);
            if (newDueDate != null && !newDueDate.trim().isEmpty()) {
                task.setDueDate(LocalDate.parse(newDueDate));
            }
            if (newStatus != null && !newStatus.trim().isEmpty()) {
                task.setStatus(Task.Status.valueOf(newStatus.toUpperCase()));
            }
            if (newPriority != null && !newPriority.trim().isEmpty()) {
                task.setPriority(Task.Priority.valueOf(newPriority.toUpperCase()));
            }
            if (newCourseCodes != null) {
                ArrayList<Course> existing = new ArrayList<>(task.getCourses());
                for (Course course : existing)
                    task.removeCourse(course);
                for (String code : newCourseCodes) {
                    Optional<Course> course = courseRepository.findByCodeAndUser(code, user);
                    if (course.isPresent())
                        task.addCourse(course.get());
                }
            }

            // log if task completed
            // if (newStatus != null && newStatus.equalsIgnoreCase("DONE")) {
            //     activityLogRepository.save(new ActivityLog(
            //             user, ActivityLog.ActionType.TASK_COMPLETED,
            //             "Completed task: " + task.getTaskName(), 10));
            // }

            if(newStatus != null && newStatus.equalsIgnoreCase("DONE")){
                activityLogRepository.save(new ActivityLog(
                        user, ActivityLog.ActionType.TASK_COMPLETED,
                        "Completed task: " + task.getTaskName(), 10));

                //streak logic
                LocalDate today = LocalDate.now();
                LocalDate lastActiveDate = user.getLastActiveDate();
                if(lastActiveDate == null){
                    //first ever task completed
                    user.setStreakCount(1);
                } else if (lastActiveDate.equals(today.minusDays(1))){
                    //keep streak going
                    user.setStreakCount(user.getStreakCount() + 1);
                } else if (lastActiveDate.equals(today)){
                    //already completed a task today - do nothing
                } else {
                    //missed streak -> streak reset
                    user.setStreakCount(1);
                }
                user.setLastActiveDate(today);
                userRepository.save(user);
            }

            return toDTO(taskRepository.save(task));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // convert Task to TaskDTO
    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.id = task.getId();
        dto.taskName = task.getTaskName();
        dto.taskType = task.getTaskType();
        dto.dueDate = task.getDueDate();
        dto.taskStatus = task.getTaskStatus().name();
        dto.priority = task.getPriority().name();
        dto.courses = task.getCourses().stream()
                .map(c -> new TaskDTO.CourseInfo(c.getId(), c.getCode(), c.getName()))
                .collect(Collectors.toList());
        return dto;
    }
}