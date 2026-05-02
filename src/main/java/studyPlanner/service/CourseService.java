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
    public Course addCourse(String name, String code) {
        Optional<Course> existing = courseRepository.findByCode(code);
        if (existing.isPresent())
            return null;
        try {
            Course course = new Course(name, code);
            return courseRepository.save(course);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // delete course
    public boolean deleteCourse(Long id) {
        Optional<Course> optional = courseRepository.findById(id);
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
    public Course editCourse(Long id, String newName, String newCode) {
    Optional<Course> optional = courseRepository.findById(id);
    if (!optional.isPresent())
        return null;
    try {
        Course course = optional.get();
        if (newName != null && !newName.trim().isEmpty()) {
            course.setName(newName);
        }
        if (newCode != null && !newCode.trim().isEmpty()) {
            course.setCode(newCode);
        }
        return courseRepository.save(course);
    } catch (IllegalArgumentException e) {
        return null;
    }
}
}