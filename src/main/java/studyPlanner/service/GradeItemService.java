package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.model.Course;
import studyPlanner.model.GradeItem;
import studyPlanner.model.User;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.GradeItemRepository;
import studyPlanner.repository.UserRepository;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;

@Service
public class GradeItemService {

    @Autowired
    private GradeItemRepository gradeItemRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // get all grade items for a course
    public List<GradeItem> getGradeItems(Long courseId) {
        User user = getCurrentUser();
        Optional<Course> course = courseRepository.findById(courseId);
        if (!course.isPresent())
            return null;
        if (!course.get().getUser().getId().equals(user.getId()))
            return null;
        return gradeItemRepository.findByCourseAndUser(course.get(), user);
    }

    // add a grade item
    public GradeItem addGradeItem(Long courseId, String name, double weight, Double grade, Double expectedGrade) {
        User user = getCurrentUser();
        Optional<Course> course = courseRepository.findById(courseId);
        if (!course.isPresent())
            return null;
        if (!course.get().getUser().getId().equals(user.getId()))
            return null;
        GradeItem item = new GradeItem(course.get(), user, name, weight, grade);
        item.setExpectedGrade(expectedGrade);
        return gradeItemRepository.save(item);
    }

    // edit a grade item
    public GradeItem editGradeItem(Long itemId, String name, Double weight, Double grade, Double expectedGrade) {
        User user = getCurrentUser();
        Optional<GradeItem> optional = gradeItemRepository.findById(itemId);
        if (!optional.isPresent())
            return null;
        GradeItem item = optional.get();
        if (!item.getUser().getId().equals(user.getId()))
            return null;
        if (name != null && !name.trim().isEmpty())
            item.setName(name);
        if (weight != null)
            item.setWeight(weight);
        item.setGrade(grade);
        item.setExpectedGrade(expectedGrade);
        return gradeItemRepository.save(item);
    }

    // delete a grade item
    public boolean deleteGradeItem(Long itemId) {
        User user = getCurrentUser();
        Optional<GradeItem> optional = gradeItemRepository.findById(itemId);
        if (!optional.isPresent())
            return false;
        if (!optional.get().getUser().getId().equals(user.getId()))
            return false;
        gradeItemRepository.deleteById(itemId);
        return true;
    }
}
