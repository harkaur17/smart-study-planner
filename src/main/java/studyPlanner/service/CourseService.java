package studyPlanner.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import studyPlanner.model.Course;
import studyPlanner.model.Task;
@Service
public class CourseService {
    private ArrayList<Course> courses = new ArrayList<>();

    //get all courses
    public ArrayList<Course> getAllCourses(){
        return new ArrayList<>(courses);
    }

    //add a course
    public boolean addCourse(String name, String code){
        if (findByCode(code) != null) return false; // duplicate check
        try{
            Course course = new Course(name, code);
            courses.add(course);
            return true;
        }
        catch(IllegalArgumentException e){
            return false;
        }
    }

    //delete course
    public boolean deleteCourse(String code){
        Course course = findByCode(code);
        if(course == null) return false;
        //remove course from all linked tasks
        ArrayList<Task> linkedTasks = course.getTasks();
        for(Task task : linkedTasks){
            task.removeCourse(course);
        }
        return courses.remove(course);
    }

    //edit a course
    public boolean editCourse(String code, String newName, String newCode){
        Course course = findByCode(code);
        if(course == null) return false;
        try{
            if (newName != null && !newName.trim().isEmpty()) {
                course.setName(newName);
            }
            if(newCode != null && !newCode.trim().isEmpty()){
                course.setCode(newCode);
            }
            return true;
        }
        catch(IllegalArgumentException e){
            return false;
        }
    }

    private Course findByCode(String code){
        for(Course c: courses){
            if(c.getCode().equalsIgnoreCase(code.trim())){
                return c;
            }
        }
        return null;
    }
}