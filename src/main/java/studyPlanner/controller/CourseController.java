package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.service.CourseService;
import studyPlanner.model.Course;
import java.util.ArrayList;

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
    public ResponseEntity<ArrayList<Course>> getAllCourses() {
        ArrayList<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses); // sends 200 with list as JSON
    }

    // POST /api/courses - add a course
    @PostMapping
    public ResponseEntity<String> addCourse(@RequestBody CourseRequest request) {
        boolean result = courseService.addCourse(request.name, request.code);
        if (result) {
            return ResponseEntity.status(201).body("Course added");
        } else {
            return ResponseEntity.badRequest().body("Failed to add course");
        }
    }

    // DELETE /api/courses/{code} - delete course
    @DeleteMapping("/{code}")
    public ResponseEntity<String> deleteCourse(@PathVariable String code) {
        boolean result = courseService.deleteCourse(code);
        if (result) {
            return ResponseEntity.ok("Course deleted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/courses/{code} - edit a course
    @PutMapping("/{code}")
    public ResponseEntity<String> editCourse(@PathVariable String code, @RequestBody CourseRequest request){
        boolean result = courseService.editCourse(code, request.newName, request.newCode);
        if (result) {
            return ResponseEntity.ok("Course updated");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
