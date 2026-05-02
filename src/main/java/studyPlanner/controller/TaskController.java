package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import studyPlanner.service.CourseService;
import studyPlanner.service.TaskService;
import studyPlanner.model.Task;
import java.util.List;

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
        public List<String> courseCodes;

        public String newName;
        public String newType;
        public String newDueDate;
        public String newStatus;
        public String newPriority;
        public List<String> newCourseCodes;
    }

    // GET /api/tasks - getAllTasks()
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks); // sends 200 with list as JSON

    }

    // POST /api/tasks - addTask()
    @PostMapping
    public ResponseEntity<Task> addTask(@RequestBody TaskRequest request) {
        Task task = taskService.addTask(request.name, request.type, request.dueDate,
                request.status, request.priority, request.courseCodes);
        if (task != null) {
            return ResponseEntity.status(201).body(task);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/tasks/{id} - editTask()
    @PutMapping("/{id}")
    public ResponseEntity<Task> editTask(@PathVariable Long id, @RequestBody TaskRequest request) {
        Task task = taskService.editTask(id, request.newName, request.newType, request.newDueDate,
                request.newStatus, request.newPriority, request.newCourseCodes);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/tasks/{id} - deleteTask()
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean result = taskService.deleteTask(id);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}