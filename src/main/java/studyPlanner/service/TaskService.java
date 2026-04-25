package studyPlanner.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import studyPlanner.model.Task;
import studyPlanner.model.Course;

@Service
public class TaskService {
    private ArrayList<Task> tasks = new ArrayList<>();

    // get all tasks
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks);
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
            tasks.add(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // delete a task
    public boolean deleteTask(String name) {
        if (name == null) {
            return false;
        }
        Task task = findTaskByName(name);
        if (task == null)
            return false;
        ArrayList<Course> linkedCourses = new ArrayList<>(task.getCourses());
        for (Course course : linkedCourses) {
            task.removeCourse(course);
        }
        return tasks.remove(task);
    }

    private Task findTaskByName(String taskName) {
        if (taskName == null)
            return null;
        else {
            return tasks.stream()
                    .filter(t -> t.getTaskName().equalsIgnoreCase(taskName.trim()))
                    .findFirst()
                    .orElse(null);
        }
    }

    // edit Task
    public boolean editTask(String name, String newName, String newType, String newDueDate,
            String newStatus, String newPriority) {
        Task task = findTaskByName(name);
        if (task == null)
            return false;
        try {
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
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}