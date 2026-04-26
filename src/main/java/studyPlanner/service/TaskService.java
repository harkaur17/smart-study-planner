package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import studyPlanner.model.Task;
import studyPlanner.model.Course;
import studyPlanner.repository.TaskRepository;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    // get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    // add a task
    public boolean addTask(String name, String type, String dueDateStr, String status, String priority) {
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
            taskRepository.save(task);
            return true;
        } catch (Exception e) {
             e.printStackTrace();
            return false;
        }
    }

    // delete a task
    public boolean deleteTask(String name) {
        Optional<Task> optional = taskRepository.findByTaskNameIgnoreCase(name);
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
    public boolean editTask(String name, String newName, String newType, String newDueDate,
            String newStatus, String newPriority) {

        Optional<Task> optional = taskRepository.findByTaskNameIgnoreCase(name);
        if (!optional.isPresent())
            return false;

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
            taskRepository.save(task); // ← this is critical, persists changes to DB
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}