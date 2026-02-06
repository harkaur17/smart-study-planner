package junit_tests;

import studyPlanner.model.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDate;

public class TestStudyPlanner {
	private StudyPlanner planner;

	@Before
	public void setUp() {
		planner = new StudyPlanner();
	}

	//Test 1: empty courses list initially
	@Test
	public void StudyPlannerTest1() {
		assertTrue(planner.getAllCourses().isEmpty());
	}

	//Test 2: add Course
	@Test
	public void StudyPlannerTest2() {
		Course course = new Course("OOP", "EECS2030");
		planner.addCourse(course);
		assertEquals(1, planner.getAllCourses().size());
		assertEquals(course, planner.getAllCourses().get(0));
	}

	//Test 3: test getAllCourses()
	@Test
	public void StudyPlannerTest3() {
		Course course = new Course("OOP", "EECS2030");
		planner.addCourse(course);
		ArrayList<Course> copy = planner.getAllCourses();
		copy.clear();  //modify the copy
		//original stays the same
		assertEquals(1,planner.getAllCourses().size());
	}

	//Test 4: No courses
	@Test
	public void StudyPlannerTest4() {
		String result = planner.displayCourses();
		assertTrue(result.contains("No Courses Available to display!"));
	}

	//Test 5: Test displayCourses()
	@Test
	public void StudyPlannerTest5() {
		planner.addCourse(new Course("OOP", "EECS2030"));
		planner.addCourse(new Course("Computer Organization","EECS2021"));

		String result = planner.displayCourses();
		assertEquals("1. EECS2030 - OOP\n"
				+ "2. EECS2021 - Computer Organization\n", result);
	}

	//Test 6: Test getAllTasks()
	@Test
	public void StudyPlannerTest6() {
		Task task = new Task("Assignment 1", "Assignment", LocalDate.of(2025, 12, 25), Task.Status.TODO);
		planner.addTask(task);
		assertEquals(1, planner.getAllTasks().size());
		assertEquals(task, planner.getAllTasks().get(0));
	}

	//Test 7: Test empty tests
	@Test
	public void StudyPlannerTest7() {
		assertTrue(planner.getAllTasks().isEmpty());
	}

	//Test 8: Test displayAllTasks() (empty)
	@Test
	public void StudyPlannerTest8() {
		String result = planner.displayAllTasks();
		assertEquals("No tasks available.", result);
	}

	//Test 9: Test displayAllTasks()
	@Test
	public void StudyPlannerTest9() {
		Task task1 = new Task("Assignment1", "Assignment", LocalDate.of(2025, 12, 28), Task.Status.TODO);
		Task task2 = new Task("Read Chapter", "Reading", Task.Status.IN_PROGRESS);

		planner.addTask(task1);
		planner.addTask(task2);

		String result = planner.displayAllTasks();
		assertEquals("1. Task: Assignment1\n"
				+ "Task Type: Assignment\n"
				+ "Due Date: 2025-12-28\n"
				+ "\n"
				+ "2. Task: Read Chapter\n"
				+ "Task Type: Reading\n"
				+ "\n", result);
	}

	//Test 10: Integration test
	@Test
	public void StudyPlannerTest10() {
		Course course1 = new Course("OOP", "EECS2030");
		Course course2 = new Course("Computer Organization","EECS2021");
		planner.addCourse(course1);
		planner.addCourse(course2);

		Task task = new Task("Final Project", "Project", LocalDate.of(2025, 12, 28), Task.Status.TODO);
		task.addCourse(course1);
		task.addCourse(course2);

		planner.addTask(task);

		assertEquals(1, planner.getAllTasks().size());
		Task retreivedTask = planner.getAllTasks().get(0);
		assertEquals(2, retreivedTask.getCourseCount());
		assertTrue(retreivedTask.getCourses().contains(course1));
		assertTrue(retreivedTask.getCourses().contains(course1));
	}

	//Test 11: Test defensive copy
	@Test
	public void StudyPlannerTest11() {
		Task task = new Task("Task", "Type", Task.Status.TODO);
		planner.addTask(task);

		ArrayList<Task> copy = planner.getAllTasks();
		copy.clear(); // Modify the copy

		// Original should still have the task
		assertEquals(1, planner.getAllTasks().size());
	}

	//Test 12: Find course by code
	@Test
	public void StudyPlannerTest12() {
		planner.addCourse(new Course("OOP", "EECS2030"));
		planner.addCourse(new Course("Computer Organization", "EECS2021"));

		Course found = planner.findCourseByCode("EECS2030");
		assertNotNull(found);
		assertEquals("OOP", found.getName());

		Course notFound = planner.findCourseByCode("CS999");
		assertNull(notFound);
	}

	//Test 13: Course Duplication Prevention
	@Test
	public void StudyPlanner13() {
		Course course1 = new Course("OOP", "EECS2030");
		planner.addCourse(course1);
		assertEquals(1, planner.getAllCourses().size());

		Course course2= new Course("Advanced OOP", "EECS2030");

		try {
			planner.addCourse(course2);
			fail("Should have thrown IllegalArgumentException");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Course code 'EECS2030' already exists with course name: OOP", e.getMessage());
		}
		// Verify only one course in list
		assertTrue(1 == planner.getAllCourses().size());
	}

	//Test 14: Test null in addCourse()
	@Test
	public void StudyPlannerTest14() {
		try {
			planner.addCourse(null);
			fail("Should have thrown IllegalArgumentException for null course");
		}
		catch(IllegalArgumentException e) {
			assertEquals("Course cannot be null.", e.getMessage());
		}
	}

	//Test 15: Update Task Status
	@Test
	public void StudyPlannerTest15() {
		Task task = new Task("Assignment 1", "Homework",LocalDate.of(2025, 12, 15), Task.Status.TODO);
		planner.addTask(task);
		assertEquals(Task.Status.TODO, planner.getAllTasks().get(0).getTaskStatus());
		task.updateStatus(Task.Status.IN_PROGRESS);
		assertEquals(Task.Status.IN_PROGRESS, task.getTaskStatus());
		assertEquals(Task.Status.IN_PROGRESS, planner.getAllTasks().get(0).getTaskStatus());
	}

	//Test 16: Update Task Status 2
	@Test
	public void StudyPlannerTest16() {
		Task task1 = new Task("Assignment 1", "Homework", Task.Status.TODO);
		Task task2 = new Task("Read Chapter", "Reading" , Task.Status.IN_PROGRESS);
		Task task3 = new Task("Quiz", "Exam", Task.Status.DONE);

		planner.addTask(task1);
		planner.addTask(task2);
		planner.addTask(task3);

		assertEquals(3, planner.getAllTasks().size());

		task1.updateStatus(Task.Status.IN_PROGRESS);
		task2.updateStatus(Task.Status.DONE);
		task3.updateStatus(Task.Status.TODO);

		assertEquals(Task.Status.IN_PROGRESS, task1.getTaskStatus());
		assertEquals(Task.Status.DONE, task2.getTaskStatus());
		assertEquals(Task.Status.TODO, task3.getTaskStatus());
	}

	//Test 17: View Tasks by Course Code
	@Test
	public void StudyPlannerTest17() {
		Course course1 = new Course("Computer Organization", "EECS2021");
		Course course2 = new Course("Advanced OOP", "EECS2030");
		Course course3 = new Course("Calculus", "MATH1013");

		planner.addCourse(course1);
		planner.addCourse(course2);
		planner.addCourse(course3);

		Task task1 = new Task("Assignment 1", "Homework", Task.Status.TODO);
		Task task2 = new Task("Midterm", "Exam", Task.Status.IN_PROGRESS);

		planner.addTask(task1);
		planner.addTask(task2);

		task1.addCourse(course1);
		task1.addCourse(course2);
		task1.addCourse(course3);

		task2.addCourse(course1);

		//Verify courses know about their tasks
		assertEquals(2, course1.getTasks().size()); // course1 has 2 tasks
		assertEquals(1, course2.getTasks().size()); // course2 has 1 task
		assertEquals(1, course3.getTasks().size()); // course3 has 1 task

		// Test 2: Verify the tasks are correct
		assertTrue(course1.getTasks().contains(task1));
		assertTrue(course1.getTasks().contains(task2));
		assertTrue(course2.getTasks().contains(task1));
		assertFalse(course2.getTasks().contains(task2)); // task2 not in course2
		assertTrue(course3.getTasks().contains(task1));

	}

	//Test 18: Test View Tasks By Status
	@Test
	public void StudyPlannerTest18() {
		Task task1 = new Task("Assignment 1", "Homework", Task.Status.TODO);
		Task task2 = new Task("Read Chapter", "Reading" , Task.Status.IN_PROGRESS);
		Task task3 = new Task("Quiz", "Exam", Task.Status.DONE);

		Course course1 = new Course("OOP", "EECS2030");
		Course course2 = new Course("Data Structures", "EECS2101");

		planner.addTask(task1);
		planner.addTask(task2);
		planner.addTask(task3);
		planner.addCourse(course1);
		planner.addCourse(course2);
		task1.addCourse(course1);
		task2.addCourse(course2);
		task3.addCourse(course1);
		task3.addCourse(course2);

		assertEquals(3, planner.getAllTasks().size());

		int todoCount = 0, inProgressCount = 0, doneCount = 0;
		for(Task task: planner.getAllTasks()) {
			switch(task.getTaskStatus()) {
			case TODO: todoCount++; break;
			case IN_PROGRESS: inProgressCount++; break;
			case DONE: doneCount++; break;
			}
		}

		assertEquals(1, todoCount);
		assertEquals(1, inProgressCount);
		assertEquals(1, doneCount);
	}

	//Test 19: Test status transition affect counts
	@Test
	public void StudyPlannerTest19() {
		Task task1 = new Task("Assignment", "Homework", Task.Status.TODO);
		Task task2 = new Task("Read Chapter", "Reading" , Task.Status.IN_PROGRESS);
		Task task3 = new Task("Quiz", "Exam", Task.Status.DONE);

		Course course1 = new Course("OOP", "EECS2030");
		Course course2 = new Course("Data Structures", "EECS2101");

		planner.addTask(task1);
		planner.addTask(task2);
		planner.addTask(task3);
		planner.addCourse(course1);
		planner.addCourse(course2);
		task1.addCourse(course1);
		task2.addCourse(course2);
		task3.addCourse(course1);
		task3.addCourse(course2);

		int todoCount = 0, inProgressCount = 0, doneCount = 0;
		for(Task task: planner.getAllTasks()) {
			switch(task.getTaskStatus()) {
			case TODO: todoCount++; break;
			case IN_PROGRESS: inProgressCount++; break;
			case DONE: doneCount++; break;
			}
		}

		assertEquals(1, todoCount);
		assertEquals(1, inProgressCount);
		assertEquals(1, doneCount);

		//change status of tasks
		task1.updateStatus(Task.Status.IN_PROGRESS);
		task2.updateStatus(Task.Status.DONE);

		int todoCount2 = 0, inProgressCount2 = 0, doneCount2 = 0;
		for(Task task: planner.getAllTasks()) {
			switch(task.getTaskStatus()) {
			case TODO: todoCount2++; break;
			case IN_PROGRESS: inProgressCount2++; break;
			case DONE: doneCount2++; break;
			}
		}

		assertEquals(0, todoCount2);
		assertEquals(1, inProgressCount2);
		assertEquals(2, doneCount2);
	}

	//Test 20: View Tasks by Type (Display tasks summary)
	@Test
	public void StudyPlannerTest20() {
		Task task1 = new Task("Assignment", "Homework", Task.Status.TODO);
		Task task2 = new Task("Read Chapter", "Homework" , Task.Status.TODO);
		Task task3 = new Task("Quiz", "Exam", Task.Status.TODO);

		Course course1 = new Course("OOP", "EECS2030");
		Course course2 = new Course("Data Structures", "EECS2101");

		planner.addTask(task1);
		planner.addTask(task2);
		planner.addTask(task3);
		planner.addCourse(course1);
		planner.addCourse(course2);

		task1.addCourse(course1);
		task2.addCourse(course2);
		task3.addCourse(course1);
		task3.addCourse(course2);

		//check correct number of tasks
		assertEquals(3, planner.getAllTasks().size());

		//Manually count task types to verify logic
		HashMap<String, Integer> counts = new HashMap<>();
		for(Task task: planner.getAllTasks()) {
			String type = task.getTaskType();
			if(counts.containsKey(type)) {
				counts.put(type, counts.get(type) + 1);
			} else {
				counts.put(type, 1);
			}
		}

		assertEquals(2, (int)counts.get("Homework"));
		assertEquals(1, (int)counts.get("Exam"));

		//Filter tasks by type manually
		ArrayList<Task> homeworkTasks = new ArrayList<>();
		for(Task task: planner.getAllTasks()) {
			if(task.getTaskType().equals("Homework")) {
				homeworkTasks.add(task);
			}
		}
		assertEquals(2, homeworkTasks.size());
		assertTrue(homeworkTasks.contains(task1));
		assertTrue(homeworkTasks.contains(task2));

		assertEquals(1, task1.getCourses().size());
		assertEquals(1, task2.getCourses().size());
		assertEquals(2, task3.getCourses().size());

		assertEquals(2, course1.getTasks().size());
		assertEquals(2, course2.getTasks().size());	
	}

	//Test 21: Upcoming Tasks
	@Test
	public void StudPlannerTest21() {
		LocalDate testDate = LocalDate.of(2026, 1, 15);

		Task overdueTask = new Task("Overdue", "Homework", testDate.minusDays(1), Task.Status.TODO);
		Task todayTask = new Task("Today", "Reading", testDate, Task.Status.IN_PROGRESS);
		Task tomorrowTask = new Task("Tomorrow", "Quiz", testDate.plusDays(1), Task.Status.DONE);
		Task in3DaysTask = new Task("In 3 Days", "Assignment", testDate.plusDays(3), Task.Status.TODO);
		Task in7DaysTask = new Task("In 7 Days", "Exam", testDate.plusDays(7), Task.Status.IN_PROGRESS);
		Task in8DaysTask = new Task("In 8 Days", "Project", testDate.plusDays(8), Task.Status.TODO);
		Task noDueDateTask = new Task("No Due Date", "Reading", Task.Status.TODO);

		Course course1 = new Course("OOP", "EECS2030");
		Course course2 = new Course("Data Structures", "EECS2101");

		planner.addCourse(course1);  planner.addCourse(course2);

		overdueTask.addCourse(course1);
		todayTask.addCourse(course2);
		in3DaysTask.addCourse(course1);
		in3DaysTask.addCourse(course2);

		planner.addTask(overdueTask);
		planner.addTask(todayTask);
		planner.addTask(tomorrowTask);
		planner.addTask(in3DaysTask);
		planner.addTask(in7DaysTask);
		planner.addTask(in8DaysTask);
		planner.addTask(noDueDateTask);

		assertEquals(7, planner.getAllTasks().size());
		
		LocalDate sevenDaysLater = testDate.plusDays(7);
		ArrayList<Task> todayTasks = new ArrayList<>();
		ArrayList<Task> tomorrowTasks = new ArrayList<>();
		ArrayList<Task> thisWeekTasks = new ArrayList<>();
		ArrayList<Task> overdueTasks = new ArrayList<>();
		
		for(Task task: planner.getAllTasks()) {
			LocalDate dueDate = task.getDueDate();
			if(dueDate != null) {
				if (dueDate.isBefore(testDate)) {
					overdueTasks.add(task);
				} else if (dueDate.isEqual(testDate)) {
					todayTasks.add(task);
				} else if (dueDate.isEqual(testDate.plusDays(1))) {
					tomorrowTasks.add(task);
				} else if (dueDate.isBefore(sevenDaysLater) || dueDate.isEqual(sevenDaysLater)) {
					thisWeekTasks.add(task);
				}
			}
		}
		
		//Test 21a: Overdue Tasks
		assertEquals(1, overdueTasks.size());
		assertTrue(overdueTasks.contains(overdueTask));
		assertFalse(overdueTasks.contains(in8DaysTask));
		assertFalse(overdueTasks.contains(noDueDateTask));

		//Test 21b: Today Tasks
		assertEquals(1, todayTasks.size());
		assertTrue(todayTasks.contains(todayTask));
		assertFalse(todayTasks.contains(in8DaysTask));
		assertFalse(todayTasks.contains(noDueDateTask));

		//Test 21c: Tomorrow Tasks
		assertEquals(1, tomorrowTasks.size());
		assertTrue(tomorrowTasks.contains(tomorrowTask));
		assertFalse(tomorrowTasks.contains(in8DaysTask));
		assertFalse(tomorrowTasks.contains(noDueDateTask));

		//Test 21d: This weeks Tasks
		assertEquals(2, thisWeekTasks.size()); // in3DaysTask and in7DaysTask
		assertTrue(thisWeekTasks.contains(in3DaysTask));
		assertTrue(thisWeekTasks.contains(in7DaysTask));
		assertFalse(thisWeekTasks.contains(in8DaysTask)); // 8 days is NOT in "this week"
		assertFalse(thisWeekTasks.contains(noDueDateTask));

		//Test 21e: Tasks that should NOT appear anywhere
		assertFalse(overdueTasks.contains(in8DaysTask));
		assertFalse(todayTasks.contains(in8DaysTask));
		assertFalse(tomorrowTasks.contains(in8DaysTask));
		assertFalse(thisWeekTasks.contains(in8DaysTask));

		assertFalse(overdueTasks.contains(noDueDateTask));
		assertFalse(todayTasks.contains(noDueDateTask));
		assertFalse(tomorrowTasks.contains(noDueDateTask));
		assertFalse(thisWeekTasks.contains(noDueDateTask));

		//Test 21f: Course relationships are maintained
		assertEquals(1, overdueTask.getCourses().size());
		assertEquals(1, todayTask.getCourses().size());
		assertEquals(2, in3DaysTask.getCourses().size());

		//Test 21g: Total upcoming count (today + tomorrow + this week)
		int totalUpcoming = todayTasks.size() + tomorrowTasks.size() + thisWeekTasks.size();
		assertEquals(4, totalUpcoming); // today + tomorrow + (3 days + 7 days)

		//Test 21h: Overdue count
		assertEquals(1, overdueTasks.size());
	}
	
	//Test 22: Upcoming Incomplete Tasks Logic
	@Test
	public void StudyPlannerTest22() {
	    LocalDate today = LocalDate.of(2026, 1, 16);
	    
	    // Create test tasks
	    Task overdueTodo = new Task("Overdue", "HW", today.minusDays(1), Task.Status.TODO);
	    Task todayTodo = new Task("Today", "Quiz", today, Task.Status.TODO);
	    Task tomorrowInProgress = new Task("Tomorrow", "Project", today.plusDays(1), Task.Status.IN_PROGRESS);
	    Task in7DaysTodo = new Task("7 Days", "Exam", today.plusDays(7), Task.Status.TODO);
	    Task in8DaysTodo = new Task("8 Days", "Project", today.plusDays(8), Task.Status.TODO);
	    Task doneTask = new Task("Done", "Reading", today, Task.Status.DONE);
	    Task noDueDate = new Task("No Due", "Reading", Task.Status.TODO);
	    
	    planner.addTask(overdueTodo);
	    planner.addTask(todayTodo);
	    planner.addTask(tomorrowInProgress);
	    planner.addTask(in7DaysTodo);
	    planner.addTask(in8DaysTodo);
	    planner.addTask(doneTask);
	    planner.addTask(noDueDate);
	    
	    // Filter logic
	    ArrayList<Task> filtered = new ArrayList<>();
	    LocalDate limit = today.plusDays(7);
	    
	    for (Task t : planner.getAllTasks()) {
	        LocalDate due = t.getDueDate();
	        Task.Status status = t.getTaskStatus();
	        
	        if (due != null && 
	            (status == Task.Status.TODO || status == Task.Status.IN_PROGRESS) &&
	            !due.isBefore(today) && 
	            !due.isAfter(limit)) {
	            filtered.add(t);
	        }
	    }
	    
	    // Sort by date
	    filtered.sort((a, b) -> a.getDueDate().compareTo(b.getDueDate()));
	    
	    // Verify results
	    assertEquals(3, filtered.size()); // todayTodo, tomorrowInProgress, in7DaysTodo
	    assertTrue(filtered.contains(todayTodo));
	    assertTrue(filtered.contains(tomorrowInProgress));
	    assertTrue(filtered.contains(in7DaysTodo));
	    
	    // Verify exclusions
	    assertFalse(filtered.contains(overdueTodo)); // overdue
	    assertFalse(filtered.contains(in8DaysTodo)); // beyond 7 days
	    assertFalse(filtered.contains(doneTask)); // DONE status
	    assertFalse(filtered.contains(noDueDate)); // no due date
	    
	    // Verify sorting order
	    assertEquals(today, filtered.get(0).getDueDate());
	    assertEquals(today.plusDays(1), filtered.get(1).getDueDate());
	    assertEquals(today.plusDays(7), filtered.get(2).getDueDate());
	}
	
	//Test 23: Upcoming Incomplete Tasks (edge cases)
	@Test
	public void StudyPlannerTest23() {
	    LocalDate today = LocalDate.of(2026, 1, 16);
	    
	    // Test 1: Empty list
	    assertTrue(planner.getAllTasks().isEmpty());
	    
	    // Test 2: Only DONE tasks
	    planner.addTask(new Task("Task1", "Type", today, Task.Status.DONE));
	    planner.addTask(new Task("Task2", "Type", today.plusDays(1), Task.Status.DONE));
	    
	    ArrayList<Task> result = filterTasks(planner.getAllTasks(), today);
	    assertEquals(0, result.size()); // No TO DO/IN_PROGRESS
	    
	    // Test 3: Only overdue
	    planner.addTask(new Task("Task3", "Type", today.minusDays(1), Task.Status.TODO));
	    result = filterTasks(planner.getAllTasks(), today);
	    assertEquals(0, result.size()); // No today/future
	    
	    // Test 4: Only beyond 7 days
	    planner.addTask(new Task("Task4", "Type", today.plusDays(8), Task.Status.TODO));
	    result = filterTasks(planner.getAllTasks(), today);
	    assertEquals(0, result.size()); // No within 7 days
	    
	    // Test 5: Boundary - exactly 7 days included
	    Task boundaryTask = new Task("Boundary", "Exam", today.plusDays(7), Task.Status.TODO);
	    planner.addTask(boundaryTask);
	    result = filterTasks(planner.getAllTasks(), today);
	    assertEquals(1, result.size()); // 7 days is included
	    assertTrue(result.contains(boundaryTask));
	}

	// Helper method matching our filter logic
	private ArrayList<Task> filterTasks(ArrayList<Task> tasks, LocalDate today) {
	    ArrayList<Task> filtered = new ArrayList<>();
	    LocalDate limit = today.plusDays(7);
	    for (Task t : tasks) {
	        LocalDate due = t.getDueDate();
	        Task.Status status = t.getTaskStatus(); 
	        if (due != null && 
	            (status == Task.Status.TODO || status == Task.Status.IN_PROGRESS) &&
	            !due.isBefore(today) && 
	            !due.isAfter(limit)) {
	            filtered.add(t);
	        }
	    }	    
	    filtered.sort((a, b) -> a.getDueDate().compareTo(b.getDueDate()));
	    return filtered;
	}
	
	//Test 24: Delete Task with Course Links
	@Test
	public void StudyPlanner24() {
		Course math = new Course("Mathematics", "MATH101");
		Course science = new Course("Science", "SCI101");
		
		Task mathHW = new Task("Math Homework", "Assignment", LocalDate.now().plusDays(1), Task.Status.TODO);
	    Task scienceQuiz = new Task("Science Quiz", "Quiz", LocalDate.now().plusDays(2), Task.Status.IN_PROGRESS);
	    Task combined = new Task("Combined", "Project", LocalDate.now().plusDays(3), Task.Status.TODO);
	    
	    //Link Tasks to Courses
	    mathHW.addCourse(math);
	    scienceQuiz.addCourse(science);
	    combined.addCourse(science);
	    combined.addCourse(math);
	    
	    // Verify initial state
	    assertEquals(1, mathHW.getCourses().size());
	    assertEquals(1, scienceQuiz.getCourses().size());
	    assertEquals(2, combined.getCourses().size());
	    assertEquals(2, math.getTasks().size()); // mathHW + combined
	    assertEquals(2, science.getTasks().size()); // scienceQuiz + combined
	    
	    // Simulate deleting mathHW task (cleanup logic only)
	    ArrayList<Course> mathHWCourses = new ArrayList<>(mathHW.getCourses());
	    for(Course course: mathHWCourses) {
	        mathHW.removeCourse(course);
	    }
	    
	    // Verify cleanup happened correctly
	    assertEquals(0, mathHW.getCourses().size()); // mathHW has no courses
	    assertEquals(1, math.getTasks().size()); // Only combined remains in math
	    assertFalse(math.getTasks().contains(mathHW));
	    assertTrue(math.getTasks().contains(combined));
	    
	    // Verify other tasks are unaffected
	    assertEquals(1, scienceQuiz.getCourses().size()); // Still has science
	    assertEquals(2, combined.getCourses().size()); // Still has math + science
	    assertTrue(combined.getCourses().contains(math));
	    assertTrue(combined.getCourses().contains(science));
	}
	
	//Test 25: Delete Course with Task Links
	@Test
	public void StudyPlannerTest25() {
	    Course math = new Course("Mathematics", "MATH101");
	    Course science = new Course("Science", "SCI101");
	   
	    Task task1 = new Task("Task1", "HW", LocalDate.now().plusDays(1), Task.Status.TODO);
	    Task task2 = new Task("Task2", "Quiz", LocalDate.now().plusDays(2), Task.Status.TODO);
	   
	    task1.addCourse(math);
	    task2.addCourse(math);
	    task2.addCourse(science);
	    
	    // Verify initial state
	    assertEquals(1, task1.getCourses().size());
	    assertEquals(2, task2.getCourses().size());
	    assertEquals(2, math.getTasks().size()); // task1 + task2
	    assertEquals(1, science.getTasks().size()); // task2
	    
	 // Simulate deleting math course (cleanup logic only)
	    ArrayList<Task> mathTasks = new ArrayList<>(math.getTasks());
	    for (Task task : mathTasks) {
	        task.removeCourse(math);
	    }
	    
	    // Verify cleanup
	    assertEquals(0, math.getTasks().size()); // math has no tasks
	    assertEquals(0, task1.getCourses().size()); // task1 has no courses
	    assertEquals(1, task2.getCourses().size()); // task2 only has science now
	    assertFalse(task2.getCourses().contains(math));
	    assertTrue(task2.getCourses().contains(science));
	    
	    // Verify science course unaffected
	    assertEquals(1, science.getTasks().size());
	    assertTrue(science.getTasks().contains(task2));
	}
	
	// Test 26: Delete Edge Cases
	@Test
	public void StudyPlannerTest26() {
	    // Test 1: Task with no courses (test Task logic only)
	    Task standalone = new Task("Standalone", "Reading", Task.Status.TODO);
	    Course someCourse = new Course("Some", "SOME101");
	    
	    assertEquals(0, standalone.getCourses().size());
	    assertTrue(someCourse.getTasks().isEmpty());
	    
	    // Try to delete
	    ArrayList<Course> courses = new ArrayList<>(standalone.getCourses());
	    for (Course c : courses) {
	        standalone.removeCourse(c);
	    }
	    assertEquals(0, standalone.getCourses().size());
	    
	    // Test 2: Course with no tasks (test Course logic only)
	    Course emptyCourse = new Course("Empty", "EMPTY101");
	    Task someTask = new Task("Some", "Type", Task.Status.TODO);
	    
	    assertEquals(0, emptyCourse.getTasks().size());
	    assertEquals(0, someTask.getCourses().size());
	    
	    // Try to delete
	    ArrayList<Task> tasks = new ArrayList<>(emptyCourse.getTasks());
	    for (Task t : tasks) {
	        emptyCourse.removeTask(t);
	    }
	    assertEquals(0, emptyCourse.getTasks().size());
	    
	    // Test 3: Remove non-existent relationships
	    // Task.removeCourse() on non-linked course should return false
	    assertFalse(standalone.removeCourse(someCourse));
	    //assertFalse(emptyCourse.removeTask(someTask));
	    
	    // Test 4: removeCourseByCode with non-existent code
	    standalone.addCourse(someCourse);
	    assertEquals(1, standalone.getCourses().size());
	    assertFalse(standalone.removeCourseByCode("NONEXISTENT"));
	    assertEquals(1, standalone.getCourses().size()); // Should still have the course
	    assertTrue(standalone.removeCourseByCode("SOME101")); // Should work
	    assertEquals(0, standalone.getCourses().size());
	}
}