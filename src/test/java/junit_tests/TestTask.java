package junit_tests;

import studyPlanner.model.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;

public class TestTask {

	// Test 1: Constructor with all fields (String date)
	@Test
	public void TestTask1() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			assertEquals("Assignment 1", task.getTaskName());
			assertEquals("Assignment", task.getTaskType());
			assertEquals(LocalDate.of(2025, 12, 25), task.getDueDate());
			assertEquals(Task.Status.TODO, task.getTaskStatus());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	// Test 2: Constructor with no due date
	@Test
	public void TestTask2() {
		try {
			Task task = new Task("Assignment 1", "Assignment", Task.Status.IN_PROGRESS);
			assertEquals("Assignment 1", task.getTaskName());
			assertEquals("Assignment", task.getTaskType());
			assertNull(task.getDueDate());
			assertEquals(Task.Status.IN_PROGRESS, task.getTaskStatus());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 3: Test update Status
	@Test
	public void TestTask3() {
		try {
			Task task1 = new Task("Assignment 1", "Assignment", Task.Status.TODO);
			Task task2 = new Task("Assignment 2", "Assignment", Task.Status.IN_PROGRESS);
			task1.setStatus(Task.Status.IN_PROGRESS);
			task2.setStatus(Task.Status.DONE);

			assertNotEquals(Task.Status.TODO, task1.getTaskStatus());
			assertEquals(Task.Status.IN_PROGRESS, task1.getTaskStatus());

			assertNotEquals(Task.Status.IN_PROGRESS, task2.getTaskStatus());
			assertEquals(Task.Status.DONE, task2.getTaskStatus());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 4: Test update Due Date
	@Test
	public void TaskTest4() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			assertEquals(LocalDate.of(2025, 12, 25), task.getDueDate());
			task.setDueDate(LocalDate.of(2025, 12, 30));
			assertNotEquals(LocalDate.of(2025, 12, 25), task.getDueDate());
			assertEquals(LocalDate.of(2025, 12, 30), task.getDueDate());	
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 5: Test Add Course
	@Test
	public void TaskTest5() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			Course course = new Course("OOP", "EECS2030");
			assertTrue(task.getCourses().isEmpty());

			task.addCourse(course);
			assertFalse(task.getCourses().isEmpty());
			assertEquals(1, task.getCourses().size());
			assertEquals(course, task.getCourses().get(0));
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 6: Test toString()
	@Test
	public void TaskTest6() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			Course course1 = new Course("OOP", "EECS2030");
			Course course2 = new Course("Computer Organization","EECS2021");
			Course course3 = new Course("Applied Calculus 2", "MATH1014");

			task.addCourse(course1);
			assertEquals(1, task.getCourses().size());
			assertEquals(course1, task.getCourses().get(0));

			task.addCourse(course2);
			assertEquals(2, task.getCourseCount());
			assertEquals(course2, task.getCourses().get(1));

			task.addCourse(course3);
			assertEquals(3, task.getCourseCount());
			assertEquals(course3, task.getCourses().get(2));

			assertEquals("Task: Assignment 1\n"
					+ "Task Type: Assignment\n"
					+ "Status: TODO\n"
					+ "Priority: MEDIUM\n"
					+ "Due Date: 2025-12-25\n"
					+ "Courses:\n"
					+ "  1. Course Name: OOP, Code: EECS2030\n"
					+ "  2. Course Name: Computer Organization, Code: EECS2021\n"
					+ "  3. Course Name: Applied Calculus 2, Code: MATH1014\n"
					, task.toString());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 7: update Due Date from null
	@Test
	public void TaskTest7() {
		try {
			Task task = new Task("Assignment 1", "Assignment", Task.Status.IN_PROGRESS);
			assertNull(task.getDueDate());
			task.setDueDate(LocalDate.of(2025, 12, 27));
			assertNotNull(task.getDueDate());
			assertEquals(LocalDate.of(2025, 12, 27), task.getDueDate());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 8: Test course count after removing/adding courses using Course object
	@Test
	public void TaskTest8() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			Course course1 = new Course("OOP", "EECS2030");
			Course course2 = new Course("Computer Organization","EECS2021");
			Course course3 = new Course("Applied Calculus 2", "MATH1014");

			task.addCourse(course1);
			assertEquals(1, task.getCourseCount());
			assertEquals(course1, task.getCourses().get(0));

			task.addCourse(course2);
			assertEquals(2, task.getCourseCount());
			assertEquals(course2, task.getCourses().get(1));

			task.removeCourse(course2);
			assertEquals(1, task.getCourses().size());
			assertEquals(course1, task.getCourses().get(0));

			task.addCourse(course3);
			assertEquals(2, task.getCourseCount());
			assertEquals(course3, task.getCourses().get(1));

		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 9: Test course count after removing/adding courses using Course Code
	@Test
	public void TaskTest9() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			Course course1 = new Course("OOP", "EECS2030");
			Course course2 = new Course("Computer Organization","EECS2021");
			Course course3 = new Course("Applied Calculus 2", "MATH1014");

			task.addCourse(course1);
			assertEquals(1, task.getCourseCount());
			assertEquals(course1, task.getCourses().get(0));

			task.addCourse(course2);
			assertEquals(2, task.getCourseCount());
			assertEquals(course2, task.getCourses().get(1));

			task.removeCourseByCode("EECS2030");
			assertEquals(1, task.getCourses().size());
			assertEquals(course2, task.getCourses().get(0));

			task.addCourse(course3);
			assertEquals(2, task.getCourseCount());
			assertEquals(course3, task.getCourses().get(1));
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 10: Test toString() without Due Date
	@Test
	public void TaskTest10() {
		try {
			Task task = new Task("Task", "Type", Task.Status.TODO);
			String result = task.toString();
			assertFalse(result.contains("Due Date:"));
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	//Test 11: Test toString() with no courses
	@Test
	public void TaskTest11() {
		try {
			Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			assertEquals("Task: Assignment 1\n"
					+ "Task Type: Assignment\n"
					+ "Status: TODO\n"
					+ "Priority: MEDIUM\n"
					+ "Due Date: 2025-12-25\n",task.toString());
		}
		catch (IllegalArgumentException e) {
			fail("Exception was not excepted");
		}
	}

	// Test 12: Null task name should throw exception
	@Test
	public void TaskTest12() {
		try {
			new Task(null, "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
			fail("Failed to through the excepted exception");
		}
		catch (IllegalArgumentException e){
			  assertTrue(e.getMessage().contains("Task name"));
		}
	}
	
	//Test 13: Empty Task Name
	@Test
	public void TaskTest13() {
	    try {
	        new Task("  ", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
	        fail("Expected IllegalArgumentException for empty task name");
	    } catch (IllegalArgumentException e) {
	        assertTrue(e.getMessage().contains("Task name"));
	    }
	}
	
	//Test 14: 
	@Test
	public void TaskTest14() {
	    try {
	        new Task("Valid Name", null, LocalDate.of(2025, 12, 25), Task.Status.TODO);
	        fail("Expected IllegalArgumentException for null task type");
	    } catch (IllegalArgumentException e) {
	        assertTrue(e.getMessage().contains("Task type"));
	    }
	}
	
	//Test 15: Null status
	@Test
	public void TaskTest15() {
	    try {
	        new Task("Valid Name", "Type", LocalDate.of(2025, 12, 25), null);
	        fail("Expected IllegalArgumentException for null status");
	    } catch (IllegalArgumentException e) {
	        assertTrue(e.getMessage().contains("Task Status"));
	    }
	}
	
	//Test 16: Null Task Name (2nd constructor)
	@Test
	public void TaskTest16() {
	    try {
	        new Task(null, "Type", Task.Status.TODO);
	        fail("Expected IllegalArgumentException for null task name (2nd constructor)");
	    } catch (IllegalArgumentException e) {
	        assertTrue(e.getMessage().contains("Task name"));
	    }
	}
	
	//Test 17: Remove non-existent course
	@Test
	public void TaskTest17() {
	    try {
	        Task task = new Task("Task", "Type", Task.Status.TODO);
	        Course course = new Course("Java", "CS101");
	        assertFalse(task.removeCourse(course));
	    } catch (IllegalArgumentException e) {
	        fail("Exception was not expected");
	    }
	}
	
	//Test 18: Remove non-existent course
	@Test
	public void TaskTest18() {
	    try {
	        Task task = new Task("Task", "Type", Task.Status.TODO);
	        task.addCourse(new Course("Java", "CS101"));
	        assertFalse(task.removeCourseByCode("CS999"));
	    } catch (IllegalArgumentException e) {
	        fail("Exception was not expected");
	    }
	}
	
	//Test 19: toString() with only one course
	@Test
	public void TaskTest19() {
	    try {
	        Task task = new Task("Task", "Type", Task.Status.TODO);
	        task.addCourse(new Course("Java", "CS101"));
	        String result = task.toString();
	        assertTrue(result.contains("1. Course Name: Java, Code: CS101"));
	        assertFalse(result.contains("2.")); // Should not have second item
	    } catch (IllegalArgumentException e) {
	        fail("Exception was not expected");
	    }
	}
	
	//Test 20: Empty task type
	@Test
	public void TaskTest20() {
	    try {
	        new Task("Valid Name", "", LocalDate.of(2025, 12, 25), Task.Status.TODO);
	        fail("Expected IllegalArgumentException for empty task type");
	    } catch (IllegalArgumentException e) {
	        assertTrue(e.getMessage().contains("Task type"));
	    }
	}
	
	//Test 21: Test addcourse bidirectional
	@Test
	public void TaskTest21() {
		Course course = new Course("OOP", "EECS2030");
		Task task = new Task("Assignment 1", "Homework", Task.Status.TODO);
		
		task.addCourse(course);
		
		assertEquals(1, task.getCourses().size());
		assertEquals(task.getCourses().get(0), course);
		
		assertEquals(1, course.getTasks().size());
		assertEquals(course.getTasks().get(0), task);
	}
	
	//Test 22: Test remove course bidirectional
	@Test
	public void TaskTest22() {
		Course course = new Course("OOP", "EECS2030");
		Task task = new Task("Assignment 1", "Homework", Task.Status.TODO);
		
		task.addCourse(course);
		assertEquals(1, task.getCourses().size());
		
		task.removeCourse(course);
		
		assertEquals(0, task.getCourses().size());
		assertEquals(0, course.getTasks().size());
	}
	
	//Test 23: Test for default priority
	@Test
	public void TaskTest23() {
		Task task = new Task("Task", "Type", Task.Status.TODO);
		assertEquals(Task.Priority.MEDIUM, task.getPriority());
	}

	//Test 24: Test for priority constructor
	@Test
	public void TaskTest24() {
		Task task = new Task("Task", "Type", Task.Status.TODO, Task.Priority.HIGH);
		assertEquals(Task.Priority.HIGH, task.getPriority());
	}

	//Task 25: Test for setPriority()
	@Test
	public void TaskTest25() {
		Task task = new Task("Task", "Type", Task.Status.TODO);
		assertEquals(Task.Priority.MEDIUM, task.getPriority());
		task.setPriority(Task.Priority.LOW);
		assertEquals(Task.Priority.LOW, task.getPriority());

	}

	//Task 26: Test for null priority throws exception
	@Test
	public void TaskTest26() {
		Task task = new Task("Task", "Type", Task.Status.TODO);
		assertEquals(Task.Priority.MEDIUM, task.getPriority());
		try {
			task.setPriority(null);
			fail("Expected IllegalArgumentException for null priority");
		}
		catch(IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("Priority"));
		}

	}
	
	//Test 27: Test for toString() containing priority
	@Test
	public void TaskTest27() {
	    Task task = new Task("Task", "Type", Task.Status.TODO, Task.Priority.HIGH);
	    assertTrue(task.toString().contains("Priority: HIGH"));
	}
}
