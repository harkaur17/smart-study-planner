package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import studyPlanner.model.Task;
import studyPlanner.dto.TaskDTO;
import studyPlanner.model.Course;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    // get all tasks
    public List<TaskDTO> getAllTasks() {
         return taskRepository.findAll().stream()
            .map(this::toDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    // add a task
    public TaskDTO addTask(String name, String type, String dueDateStr, String status,
            String priority, List<String> courseCodes) {
        try {
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());

            LocalDate dueDate = null;
            if (dueDateStr != null && !dueDateStr.trim().isEmpty()) {
                dueDate = LocalDate.parse(dueDateStr);
            }

            Task task;
            if (dueDate != null) {
                task = new Task(name, type, dueDate, taskStatus, taskPriority);
            } else {
                task = new Task(name, type, taskStatus, taskPriority);
            }

            // link courses
            if (courseCodes != null) {
                for (String code : courseCodes) {
                    Optional<Course> course = courseRepository.findByCode(code);
                    if (course.isPresent()) {
                        task.addCourse(course.get());
                    }
                }
            }

            return toDTO(taskRepository.save(task));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // delete a task
    public boolean deleteTask(Long id) {
        Optional<Task> optional = taskRepository.findById(id);
        if (!optional.isPresent()) {
            return false;
        }
        Task task = optional.get();
        ArrayList<Course> linkedCourses = new ArrayList<>(task.getCourses());
        for (Course course : linkedCourses) {
            task.removeCourse(course);
        }
        taskRepository.delete(task);
        return true;
    }

    // edit Task
    public TaskDTO editTask(Long id, String newName, String newType, String newDueDate,
            String newStatus, String newPriority, List<String> newCourseCodes) {

        Optional<Task> optional = taskRepository.findById(id);
        if (!optional.isPresent())
            return null;

        try {
            Task task = optional.get();
            if (newName != null && !newName.trim().isEmpty()) {
                task.setName(newName);
            }
            if (newType != null && !newType.trim().isEmpty()) {
                task.setTaskType(newType);
            }
            if (newDueDate != null && !newDueDate.trim().isEmpty()) {
                LocalDate dueDate = LocalDate.parse(newDueDate);
                task.setDueDate(dueDate);
            }
            if (newStatus != null && !newStatus.trim().isEmpty()) {
                Task.Status taskStatus = Task.Status.valueOf(newStatus.toUpperCase());
                task.setStatus(taskStatus);
            }
            if (newPriority != null && !newPriority.trim().isEmpty()) {
                Task.Priority taskPriority = Task.Priority.valueOf(newPriority.toUpperCase());
                task.setPriority(taskPriority);
            }
            // update courses
            if (newCourseCodes != null) {

                ArrayList<Course> existingCourses = new ArrayList<>(task.getCourses());
                for (Course course : existingCourses) {
                    task.removeCourse(course);
                }
                for (String code : newCourseCodes) {
                    Optional<Course> course = courseRepository.findByCode(code);
                    if (course.isPresent()) {
                        task.addCourse(course.get());
                    }
                }
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
                .collect(java.util.stream.Collectors.toList());
        return dto;
    }
}