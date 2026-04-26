package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import studyPlanner.model.Course;
import studyPlanner.model.Task;
import studyPlanner.repository.CourseRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    // get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // add a course
    public boolean addCourse(String name, String code) {
        Optional<Course> existing = courseRepository.findByCode(code);
        if (existing.isPresent())
            return false;
        try {
            Course course = new Course(name, code);
            courseRepository.save(course);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // delete course
    public boolean deleteCourse(String code) {
        Optional<Course> optional = courseRepository.findByCode(code);
        if (!optional.isPresent())
            return false;
        Course course = optional.get();
        for (Task task : course.getTasks()) {
            task.removeCourse(course);
        }
        courseRepository.delete(course);
        return true;
    }

    // edit a course
    public boolean editCourse(String code, String newName, String newCode) {
        Optional<Course> optional = courseRepository.findByCode(code);
        if (!optional.isPresent())
            return false;
        try {
            Course course = optional.get();
            if (newName != null && !newName.trim().isEmpty()) {
                course.setName(newName);
            }
            if (newCode != null && !newCode.trim().isEmpty()) {
                course.setCode(newCode);
            }
            courseRepository.save(course);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}