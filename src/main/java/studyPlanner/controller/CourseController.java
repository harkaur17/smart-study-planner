package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.service.CourseService;
import studyPlanner.model.Course;
import java.util.List;
import studyPlanner.dto.TaskDTO;

@RestController // tells spring that this handles HTTP requests
@RequestMapping("/api/courses") // sets the base URL

public class CourseController {

    @Autowired // tells spring to automatcially inject the CourseService into controller
    private CourseService courseService;

    static class CourseRequest {
        public String name;
        public String code;
        public String newName;
        public String newCode;
    }

    // GET /api/courses - fetch all courses
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses); // sends 200 with list as JSON
    }

    // POST /api/courses - add a course
    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody CourseRequest request) {
        Course course = courseService.addCourse(request.name, request.code);
        if (course != null) {
            return ResponseEntity.status(201).body(course);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/courses/{id} - delete course
    @DeleteMapping("/{id}")
    public ResponseEntity<Course> deleteCourse(@PathVariable Long id) {
        boolean result = courseService.deleteCourse(id);
        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/courses/{id} - edit a course
    @PutMapping("/{id}")
    public ResponseEntity<Course> editCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        Course course = courseService.editCourse(id, request.newName, request.newCode);
        if (course != null) {
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {
        Course course = courseService.getCourse(id);
        if (course == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(course);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskDTO>> getCourseTasks(@PathVariable Long id) {
        List<TaskDTO> tasks = courseService.getTasksForCourse(id);
        if (tasks == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tasks);
    }
}
