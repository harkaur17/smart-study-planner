package junit_tests;

import studyPlanner.model.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class TestCourse {
    
    @Test
    public void testCourse1() { 
        Course course = new Course("Java", "CS101");
        assertEquals("Java", course.getName());
    }
    
    @Test
    public void testCourse2() { 
        Course course = new Course("Java", "CS101");
        assertEquals("CS101", course.getCode());
    }
    
    @Test
    public void testCourse3() { 
        Course course = new Course("Java", "CS101");
        assertEquals("Course Name: Java, Code: CS101", course.toString());
    }
    
    //Test 4: add Task
    @Test
    public void testCourse4() {
    	Course course = new Course("Java", "EECS2030");
    	Task task = new Task("Assignment", "Homework", Task.Status.TODO);
    	
    	course.addTask(task);
    	assertEquals(1, course.getTasks().size());
    	assertTrue(course.getTasks().contains(task));
    }
    
    //Test 5: remove task
    @Test
    public void testCourse5() {
    	Course course = new Course("Java", "EECS2030");
    	Task task = new Task("Assignment", "Homework", Task.Status.TODO);
    	
    	course.addTask(task);
    	assertEquals(1, course.getTasks().size());
        
        course.removeTask(task);
        assertEquals(0, course.getTasks().size());
    }
    
    //Test 6: defensive copy of tasks
    @Test
    public void testCourse6() {
    	Course course = new Course("Java", "EECS2030");
    	Task task = new Task("Assignment", "Homework", Task.Status.TODO);
    	
    	course.addTask(task);
    	ArrayList<Task> copy = course.getTasks();
    	copy.clear();
    	assertEquals(1, course.getTasks().size());
    }
    
//    @Test
//    public void constructor_HandlesNullValues() {
//        Course course = new Course(null, null);
//        assertNull(course.getName());
//        assertNull(course.getCode());
//    }
}