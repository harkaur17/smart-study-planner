package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import studyPlanner.service.CourseService;
import studyPlanner.service.TaskService;
import studyPlanner.model.Task;
import java.util.ArrayList;

@RestController // tells spring that this handles HTTP requests
@RequestMapping("/api/tasks") // sets the base URL
public class TaskController {

    @Autowired // tells spring to automatcially inject the TaskService into controller
    private TaskService taskService;

    static class TaskRequest {
        public String name;
        public String type;
        public String dueDate;
        public String status;
        public String priority;

        public String newName;
        public String newType;
        public String newDueDate;
        public String newStatus;
        public String newPriority;
    }

    // GET /api/tasks - getAllTasks()
    @GetMapping
    public ResponseEntity<ArrayList<Task>> getAllTasks() {
        ArrayList<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks); // sends 200 with list as JSON

    }

    // POST /api/tasks - addTask()
    @PostMapping
    public ResponseEntity<String> addTask(@RequestBody TaskRequest request) {
        boolean result = taskService.addTask(request.name, request.type, request.dueDate, request.status,
                request.priority);
        if (result) {
            return ResponseEntity.status(201).body("Task added");
        } else {
            return ResponseEntity.badRequest().body("Failed to add task");
        }
    }

    // PUT /api/tasks/{name} - editTask()
    @PutMapping("/{name}")
    public ResponseEntity<String> editTask(@PathVariable String name, @RequestBody TaskRequest request) {
        boolean result = taskService.editTask(name, request.newName, request.newType, request.newDueDate,
                request.newStatus, request.newPriority);
        if (result) {
            return ResponseEntity.ok("Task updated");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/tasks/{name} - deleteTask()
    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteTask(@PathVariable String name) {
        boolean result = taskService.deleteTask(name);
        if (result) {
            return ResponseEntity.ok("Task deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
