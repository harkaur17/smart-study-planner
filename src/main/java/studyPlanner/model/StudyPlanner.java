package studyPlanner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import studyPlanner.model.Task.Priority;
import studyPlanner.persistence.*;

public class StudyPlanner {

	private ArrayList<Course>courses;
	private ArrayList<Task>tasks;

	private static final String DATA_FILE = "studyplanner_data.json";

	public StudyPlanner() {
		this.courses = new ArrayList<Course>();
		this.tasks = new ArrayList<Task>();
	}

	public void addCourse(Course c) {
		if(c == null) {
			throw new IllegalArgumentException("Course cannot be null.");
		}
		Course existing = findCourseByCode(c.getCode());
		if(existing != null) {
			throw new IllegalArgumentException("Course code '" + c.getCode() + "' already exists with course name: " + existing.getName());
		}
		this.courses.add(c);
	}

	public void addTask(Task t) {
		this.tasks.add(t);
	}

	public ArrayList<Course> getAllCourses() {
		//return this.courses; NO!!
		//returning a new copied list
		return new ArrayList<Course>(this.courses);
	}

	public ArrayList<Task> getAllTasks() {
		return new ArrayList<Task>(this.tasks);
	}

	//Helper method
	public Course findCourseByCode(String courseCode) {
		if (courseCode == null) return null;

		String searchCode = courseCode.trim().toUpperCase();
		for (Course c : this.courses) {
			if (c.getCode().toUpperCase().equals(searchCode)) {
				return c;
			}
		}
		return null; // Not found
	}

	public String displayCourses() {
		StringBuilder s = new StringBuilder();
		if(this.courses.isEmpty()) {
			s.append("No Courses Available to display!");
			return s.toString();
		}
		for(int i = 0; i<this.courses.size(); i++) {
			s.append(String.format("%d. %s - %s\n",i+1, this.courses.get(i).getCode(), this.courses.get(i).getName()));
		}
		return s.toString();
	}

	public String displayAllTasks() {
		StringBuilder sb = new StringBuilder();
		if (this.tasks.isEmpty()) {
			sb.append("No tasks available.");
		} else {
			// sb.append("=== All Tasks ===\n");
			for (int i = 0; i < this.tasks.size(); i++) {
				sb.append(String.format("%d. %s\n", i + 1, this.tasks.get(i).toString()));
			}
		}
		return sb.toString();
	}

	//Methods to handle ?

	public void run() {
		Scanner scanner = new Scanner(System.in);
		boolean keepRunning = true;

		System.out.println("\nLoading saved data...");
		StudyPlanner loaded = FileManager.loadData(DATA_FILE);
		this.replaceData(loaded);
		System.out.println("Loaded " + this.courses.size() + " courses and " + this.tasks.size() + " tasks.");

		while(keepRunning) {
			this.showMenu();
			String choice = scanner.nextLine();

			keepRunning = handleChoice(choice, scanner);
		}
		scanner.close();
		System.out.println("Thank you for using Smart Study Planner!");
	}

	public void showMenu() {
		System.out.println();
		System.out.println("-------------------");
		System.out.println("SMART STUDY PLANNER");
		System.out.println("-------------------");
		System.out.println("1. Add Course");
		System.out.println("2. Add Task");
		System.out.println("3. View All Courses");
		System.out.println("4. View All Tasks");
		System.out.println("5. Update Task Status");
		System.out.println("6. View Tasks by Course");
		System.out.println("7. View Tasks by Status");
		System.out.println("8. View Tasks by Type");
		System.out.println("9. View Tasks by Priority");
		System.out.println("10. View Upcoming Tasks");
		System.out.println("11. View Upcoming Incomplete Tasks");
		System.out.println("12. Delete Task");
		System.out.println("13. Delete Course");
		System.out.println("14. Edit Task");
		System.out.println("15. Edit Course");
		//
		System.out.println("19. Save Data");
		System.out.println("20. Exit"); //update exit
		System.out.println("Enter choice (1-20): ");
	}

	public boolean handleChoice(String choice, Scanner scanner){

		switch(choice) {
		case "1":
			//System.out.println("Add Course selected");
			addCourseUI(scanner);
			return true;

		case "2":
			//System.out.println("Add Task selected");
			addTaskUI(scanner);
			return true;

		case "3":
			System.out.println(displayCourses());
			return true;

		case "4":
			System.out.println(displayAllTasks());
			return true;

		case "5":
			updateTaskStatusUI(scanner);
			return true;

		case "6":
			viewTasksByCourseCodeUI(scanner);
			return true;

		case "7":
			viewTasksByStatusUI(scanner);
			return true;

		case "8":
			viewTasksByTypeUI(scanner);
			return true;
			
		case "9":
			viewTasksByPriorityUI(scanner);
			return true;

		case "10":
			viewUpcomingTasksUI(scanner);
			return true;

		case "11":
			viewUpcomingIncompleteTasksUI(scanner);
			return true;

		case "12":
			deleteTaskUI(scanner);
			return true;

		case "13":
			deleteCourseUI(scanner);
			return true;

		case "14":
			editTaskUI(scanner);
			return true;

		case "15":
			editCourseUI(scanner);
			return true;

		case "19":
			saveDataUI(scanner);
			return true;

		case "20":
			saveDataOnExit();
			return false; //ends the program

		default:
			System.out.println("Invalid choice. Please enter 1-20.");
			return true;
		}
	}

	//ADD COURSE
	public void addCourseUI(Scanner scanner) {
		System.out.println("\nAdd New Course");

		//Ask user for course name
		System.out.println("Enter Course Name: ");
		String name = scanner.nextLine();

		//Ask user for course code
		System.out.println("Enter Course Code: ");
		String code = scanner.nextLine();

		if (name.trim().isEmpty() || code.trim().isEmpty()) {
			System.out.println("Error: Course name and code cannot be empty.");
			return;
		}

		Course existingCourse = findCourseByCode(code);
		if(existingCourse != null) {
			System.out.println("Error: Course with code '" + code + "'already exists with course name:" + existingCourse.getName());
			return;
		}

		try {
			Course course = new Course(name, code);
			this.addCourse(course);
			System.out.println("Course added successfully.");
		}
		catch (IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	//ADD TASK
	public void addTaskUI(Scanner scanner) {
		System.out.println("\nAdd New Task");

		//Ask user for task name
		System.out.println("Enter Task Name: ");
		String name = scanner.nextLine();
		if (name.trim().isEmpty()) {
			System.out.println("Error: Task name cannot be empty.");
			return;  // Exit the method
		}

		//Ask user for task type
		System.out.println("Enter Task Type: ");
		String type = scanner.nextLine();
		if (type.trim().isEmpty()) {
			System.out.println("Error: Task type cannot be empty.");
			return;  // Exit the method
		}

		//Ask user for due Date (if any)
		String dueDate = null;
		System.out.println("Enter Due Date (YYYY-MM-DD) or press Enter for none: ");
		while(true) {
			String dueDateInput = scanner.nextLine();

			if(dueDateInput.trim().isEmpty() == true) {
				dueDate = null;
				break; //no due date for the task
			}

			//validate format
			if (dueDateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
				dueDate = dueDateInput;
				break;
			}
			else {
				System.out.println("Invalid format! Must be YYYY-MM-DD. Try again or press Enter for none: ");
			}
		}

		//Ask user for task status
		Task.Status status = selectStatusUI(scanner);

		//Ask user for task priority
		Task.Priority priority = selectPriorityUI(scanner);

		//Ask user for any courses to add
		ArrayList<Course> selectedCourses = selectCoursesUI(scanner);

		//display selected courses
		if(!selectedCourses.isEmpty()) {
			displaySelectedCourses(selectedCourses);
		} else {
			System.out.println("No courses selected for this task.");
		}

		try {
			Task task;
			if(dueDate == null) {
				task = new Task(name, type, status, priority);
			}
			else {
				LocalDate parsedDate = LocalDate.parse(dueDate);
				task = new Task(name, type, parsedDate, status, priority);
			}

			//Add selected courses to the task
			for(Course course: selectedCourses) {
				task.addCourse(course);
			}

			//Add to planner
			addTask(task);
			System.out.println("Task '" + name + "' added successfully!");
		}
		catch (IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private Task.Status selectStatusUI(Scanner scanner){
		System.out.println("\nSelect Task Status");
		System.out.println("1. TODO");
		System.out.println("2. IN_PROGRESS");
		System.out.println("3. DONE");
		System.out.println("Enter choice: (1-3): ");

		while (true) {
			String input = scanner.nextLine().trim();
			switch (input) {
			case "1":
				return Task.Status.TODO;
			case "2":
				return Task.Status.IN_PROGRESS;
			case "3":
				return Task.Status.DONE;
			default:
				System.out.print("Invalid choice. Please enter 1-3: ");
			}
		}
	}

	private Task.Priority selectPriorityUI(Scanner scanner){
		System.out.println("\nSelect Task Priority");
		System.out.println("1. HIGH");
		System.out.println("2. MEDIUM");
		System.out.println("3. LOW");
		System.out.println("Enter choice: (1-3): ");

		while (true) {
			String input = scanner.nextLine().trim();
			switch (input) {
			case "1":
				return Task.Priority.HIGH;
			case "2":
				return Task.Priority.MEDIUM;
			case "3":
				return Task.Priority.LOW;
			default:
				System.out.print("Invalid choice. Please enter 1-3: ");
			}
		}
	}

	//DISPLAY SELECTED COURSE
	private ArrayList<Course> selectCoursesUI(Scanner scanner){
		ArrayList<Course> selectedCourses = new ArrayList<>();

		//Check if there are any courses
		if(this.courses.isEmpty()) {
			System.out.println("No courses available. Please add courses first");
			return selectedCourses;
		}
		else {
			System.out.println("\nAvailable Courses");
			System.out.println(displayCourses());
			System.out.println("Enter course numbers to link (comma-separated & 0 for none): ");

			while(true) {
				String input = scanner.nextLine().trim();

				if(input.equals("0")) {
					//System.out.println("No courses selected.");
					return selectedCourses;
				}

				//Parse comma separated numbers
				String[] numberStrings = input.split(",");
				boolean allValid = true;
				selectedCourses.clear(); //clear previous selections

				for(String nstr: numberStrings) {
					try {
						int choice = Integer.parseInt(nstr.trim());
						if(choice >= 1 && choice <= this.courses.size()) {
							selectedCourses.add(courses.get(choice - 1));
						}
						else {
							System.out.println("Invalid number: " + choice + ". Please enter numbers 1-" + this.courses.size());
							allValid = false;
							break;
						}
					}
					catch (NumberFormatException e) {
						System.out.println("Invalid input: '" + nstr + "'. Please enter numbers only.");
						allValid = false;
						break;
					}
				}

				if(allValid) {
					System.out.println("Selected " + selectedCourses.size() + " course(s).");
					return selectedCourses;
				}
				else {
					System.out.println("Please try again.");
				}
			}
			//return selectedCourses;
		}
	}

	private void displaySelectedCourses(ArrayList<Course> selectedCourses) {
		System.out.println("\nSELECTED COURSES:");
		for(int i = 0; i<selectedCourses.size(); i++) {
			if(i > 0) System.out.print(", ");
			System.out.print(selectedCourses.get(i).getName() + " (" + selectedCourses.get(i).getCode() + ")");
		}
		System.out.println();
	}

	//UPDATE TASK STATUS
	private void updateTaskStatusUI(Scanner scanner) {
		if(this.tasks.isEmpty()) {
			System.out.println("No tasks available to update.");
			return;
		}
		System.out.println("Available Tasks");
		System.out.println("----------------");
		System.out.println(this.displayAllTasks());
		System.out.println("Select task (1-" + tasks.size() + "): ");
		int taskNumber = -1;
		try {
			String input = scanner.nextLine();
			taskNumber = Integer.parseInt(input);
			if(taskNumber < 1 || taskNumber > tasks.size()) {
				System.out.println("Invalid task number. Please enter 1-" + tasks.size());
				return;
			}
		}
		catch (NumberFormatException e) {
			System.out.println("Please a enter valid number.");
			return;
		}

		Task selectedTask = tasks.get(taskNumber -1);
		Task.Status oldStatus = selectedTask.getTaskStatus();
		// Show current status
		System.out.println("\nSelected Task: " + selectedTask.getTaskName());
		System.out.println("Current Status: " + selectedTask.getTaskStatus());
		System.out.println("\nSelect new status:");
		Task.Status newStatus = selectStatusUI(scanner);

		selectedTask.setStatus(newStatus);

		System.out.println("\nTask '" + selectedTask.getTaskName() + 
				"' status updated from " + oldStatus + 
				" to " + newStatus);
	}

	//TODO: ADD UPDATE TASK PRIORITY

	//VIEW TASKS BY COURSE CODE
	private void viewTasksByCourseCodeUI(Scanner scanner) {
		if(this.courses.isEmpty()) {
			System.out.println("No courses available to display.");
			return;
		}
		System.out.println("\nAvailable Courses:");
		System.out.println(displayCourses());

		int courseNumber = getCourseSelection(scanner, courses.size());
		if(courseNumber == -1) return; 
		Course selectedCourse = this.courses.get(courseNumber -1);
		System.out.println("\n"+selectedCourse.displayTasks());
	}

	private int getCourseSelection(Scanner scanner, int maxCourses) {
		System.out.println("Select course number (1-" + maxCourses + ") or 0 to cancel: ");
		while (true){
			String input = scanner.nextLine().trim();

			if(input.equals("0")) { 
				System.out.println("Operation Cancelled!");
				return -1;
			}
			try {
				int choice = Integer.parseInt(input);
				if (choice >= 1 && choice <= maxCourses){
					return choice;
				} else {
					System.out.println("Invalid number. Please enter 1-" + maxCourses + " or 0 to cancel: ");
				}
			}
			catch (NumberFormatException e) {
				System.out.println("Please enter a valid number");
			}
		}

	}

	//VIEW TASK BY STATUS
	private void viewTasksByStatusUI(Scanner scanner) {
		if(this.tasks.isEmpty()) {
			System.out.println("No tasks to display!");
			return;
		}

		System.out.println("Task Status Summary:");
		int[] counts = countTasksByStatus();
		System.out.println("TODO: " + counts[0] + " tasks");
		System.out.println("IN_PROGRESS: " + counts[1] + " tasks");
		System.out.println("DONE: " + counts[2] + " tasks");

		System.out.println("\nSelect Task Status");
		System.out.println("1. TODO");
		System.out.println("2. IN_PROGRESS");
		System.out.println("3. DONE");
		System.out.println("4. Back to menu");
		System.out.println("Enter choice: (1-4): ");

		Task.Status status = null;
		while (true) {
			String input = scanner.nextLine().trim();
			if(input.equals("1")) {
				status = Task.Status.TODO;
				break;
			} else if (input.equals("2")) {
				status = Task.Status.IN_PROGRESS;
				break;
			} else if (input.equals("3")){
				status = Task.Status.DONE;
				break;
			} else if (input.equals("4")) {
				return;
			} else {
				System.out.println("Invalid option! Please enter 1-4: ");
				continue;
			}
		}
		int count = 1;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i<this.tasks.size(); i++) {
			if(this.tasks.get(i).getTaskStatus().equals(status)) {
				Task task = this.tasks.get(i);
				sb.append(String.format("%d. %s [%s] - %s | Priority: %s", 
						count, 
						task.getTaskName(),
						task.getTaskType(), 
						task.getTaskStatus(), 
						task.getPriority()));
				if(task.getDueDate() != null) {
					sb.append(String.format(" (Due: %s)", task.getDueDate().toString()));
				}
				sb.append("\n");
				count++;
			}
		}
		if(count == 1) {
			System.out.println("No " + status + " tasks found.");
			return;
		}
		else {
			System.out.print(sb.toString());
		}
	}

	private int[] countTasksByStatus() {
		int[] counts = new int[3];
		for(int i = 0; i<this.tasks.size(); i++) {
			if(this.tasks.get(i).getTaskStatus().equals(Task.Status.TODO)) {
				counts[0]++;
			}
			else if (this.tasks.get(i).getTaskStatus().equals(Task.Status.IN_PROGRESS)) {
				counts[1]++;
			}
			else if (this.tasks.get(i).getTaskStatus().equals(Task.Status.DONE)) {
				counts[2]++;
			}
		}
		return counts;
	}

	//VIEW TASKS BY PRIORITY
	private void viewTasksByPriorityUI(Scanner scanner) {
		if(this.tasks.isEmpty()) {
			System.out.println("No tasks to display!");
			return;
		}
		
		System.out.println("Task Priority Summary:");
		int[] counts = countTasksByPriority();
		System.out.println("HIGH: " + counts[0] + " tasks");
		System.out.println("MEDIUM: " + counts[1] + " tasks");
		System.out.println("LOW: " + counts[2] + " tasks");

		System.out.println("\nVIEW TASKS BY PRIORITY");
		System.out.println("1. HIGH");
		System.out.println("2. MEDIUM");
		System.out.println("3. LOW");
		System.out.println("4. Back to menu");
		System.out.print("Enter choice (1-4): ");

		Task.Priority selectedPriority = null;
		while(true) {
			String input = scanner.nextLine().trim();

			switch(input) {
			case "1":
				selectedPriority = Task.Priority.HIGH;
				break;
			case "2":
				selectedPriority = Task.Priority.MEDIUM;
				break;
			case "3":
				selectedPriority = Task.Priority.LOW;
				break;
			case "4":
				return;  // Back to menu
			default:
				System.out.print("Invalid choice. Please enter 1-4: ");
				continue;
			}
			break;
		}

		int count = 0;
		for(Task task : this.tasks) {
			if(task.getPriority() == selectedPriority) {
				count++;
				System.out.printf("%d. %s [%s] - %s",
						count, task.getTaskName(), task.getTaskType(), task.getTaskStatus());
				if(task.getDueDate() != null) {
					System.out.printf(" (Due: %s)", task.getDueDate());
				}
				if(!task.getCourses().isEmpty()) {
					System.out.print(" [Courses: ");
					for(int i = 0; i < task.getCourses().size(); i++) {
						if(i > 0) System.out.print(", ");
						System.out.print(task.getCourses().get(i).getCode());
					}
					System.out.print("]");
				}
				System.out.println();
			}
		}
		
		if(count == 0) {
			System.out.println("No " + selectedPriority + " priority tasks found.");
	    } else {
	        System.out.println("\nFound " + count + " task(s) with " + selectedPriority + " priority.");
	    }
	}
	
	private int[] countTasksByPriority() {
		int[] counts = new int[3];
		for(int i = 0; i<this.tasks.size(); i++) {
			if(this.tasks.get(i).getPriority().equals(Task.Priority.HIGH)) {
				counts[0]++;
			}
			else if (this.tasks.get(i).getPriority().equals(Task.Priority.MEDIUM)) {
				counts[1]++;
			}
			else if (this.tasks.get(i).getPriority().equals(Task.Priority.LOW)) {
				counts[2]++;
			}
		}
		return counts;
	}


//VIEW TASKS BY TYPE
private void viewTasksByTypeUI(Scanner scanner) {
	if(this.tasks.isEmpty()) {
		System.out.println("No tasks to display!");
		return;
	}

	//get type summary
	HashMap<String, Integer> typeCounts = getTaskTypeSummary();
	displayTasksTypeSummary(typeCounts);

	ArrayList<String> typeList = new ArrayList<> (typeCounts.keySet());
	System.out.println("\nSelect Task type to view details");
	System.out.println("0. Back to Menu");
	for(int i =0; i<typeList.size(); i++) {
		System.out.println( i+1 + ". " + typeList.get(i) + " (" + typeCounts.get(typeList.get(i)) + " tasks)" );
	}
	while(true) {
		try {
			System.out.println("Enter choice (0-" + typeList.size() + "): ");
			String input = scanner.nextLine().trim();
			int choice = Integer.parseInt(input);
			//switch case or if-else to check the input and do accordingly
			if(choice == 0) {
				return;
			} else if(choice >= 1 && choice <= typeList.size()) {
				String selectedType = typeList.get(choice - 1);
				displayTasksByType(selectedType);
				return;
			} else {
				System.out.println("Invalid choice. Please enter 0-" + typeList.size() + ": ");
			}
		}
		catch(NumberFormatException e) {
			System.out.println("Please enter a valid number.");
		}
	}

}

private HashMap<String, Integer> getTaskTypeSummary(){
	HashMap<String, Integer> typeCounts = new HashMap<>();

	for(Task task: this.getAllTasks()) {
		String type = task.getTaskType();
		if(typeCounts.containsKey(type)) {
			typeCounts.put(type, typeCounts.get(type) + 1);
		}
		else {
			typeCounts.put(type, 1);
		}
	}
	return typeCounts;
}

private void displayTasksTypeSummary(HashMap<String, Integer> typeCounts) {
	if(typeCounts.isEmpty()) {
		System.out.println("No task types found");
		return;
	}

	//convert to sorted list
	ArrayList<String> sortedTypes = new ArrayList<>(typeCounts.keySet());
	System.out.println("\nTask Type Summary:");
	for(String type: sortedTypes) {
		Integer count = typeCounts.get(type);
		System.out.println(type + ": " + count + " task(s)");
	}
}

private void displayTasksByType(String type) {
	int count = 0;
	StringBuilder sb = new StringBuilder();
	for(Task task: this.tasks) {
		if(task.getTaskType().equals(type)) {
			count++;
			sb.append(String.format("%d. %s - %s | Priority: %s",
					count, 
					task.getTaskName(), 
					task.getTaskStatus(),
					task.getPriority()));
			if(task.getDueDate() != null) {
				sb.append(String.format(" (Due: %s)",task.getDueDate().toString()));
			}
			//Courses for a particular task
			if(!task.getCourses().isEmpty()) {
				sb.append("[Courses: ");
				for(int i = 0; i< task.getCourseCount(); i++) {
					if(i>0) sb.append(",");
					sb.append(task.getCourses().get(i).getName());
				}
				sb.append("]");
			}
			sb.append("\n");
		}
	}
	if(count == 0) {
		System.out.println("No tasks found for type " + type);
	} else {
		System.out.println("\n" + type + " Tasks (" + count + " found)");
		System.out.print(sb.toString());
	}

}

//TO VIEW UPCOMING TASKS
private void viewUpcomingTasksUI(Scanner scanner) {
	if(this.tasks.isEmpty()){
		System.out.println("No tasks to display");
		return;
	}
	System.out.println("\nUpcoming Tasks (Next 7 Days):");

	LocalDate today = LocalDate.now();
	LocalDate sevenDaysLater = today.plusDays(7);

	ArrayList<Task> todayTasks = new ArrayList<>();
	ArrayList<Task> tomorrowTasks = new ArrayList<>();
	ArrayList<Task> thisWeekTasks = new ArrayList<>();
	ArrayList<Task> overdueTasks = new ArrayList<>();

	//Loop through the tasks and categorize them
	for(int i = 0; i<this.tasks.size(); i++) {
		Task t = this.tasks.get(i);
		if(t.getDueDate() != null) {
			if(t.getDueDate().isBefore(today)) {
				//overdue tasks
				overdueTasks.add(t);
			} 
			else if(t.getDueDate().isEqual(today)) {
				//today tasks
				todayTasks.add(t);
			}
			else if(t.getDueDate().isEqual(today.plusDays(1))){
				//tomorrow tasks
				tomorrowTasks.add(t);
			}
			else if(t.getDueDate().isBefore(sevenDaysLater) || t.getDueDate().isEqual(sevenDaysLater)) {
				//this weeks tasks (including 7th day tasks)
				thisWeekTasks.add(t);
			}
		}
	}

	//display results
	displayUpcomingTasks("Today", todayTasks, today);
	displayUpcomingTasks("Tomorrow", tomorrowTasks, today);
	displayUpcomingTasks("Next 7 Days", thisWeekTasks, today);
	displayUpcomingTasks("Overdue", overdueTasks, today);

	int totalUpcoming = todayTasks.size() + tomorrowTasks.size() + thisWeekTasks.size();
	System.out.println("\nSummary: " + totalUpcoming + " upcoming task(s)");
	if(overdueTasks.size() > 0) {
		System.out.println("Warning: " + overdueTasks.size() + " over due task(s)!");  
	}
}

public void displayUpcomingTasks(String category, ArrayList<Task> tasks, LocalDate today) {
	if(tasks.isEmpty()) {
		return;
	}

	System.out.println("--- "+ category + " ---");
	StringBuilder sb = new StringBuilder();
	for(int i = 0; i<tasks.size(); i++) {
		Task task = tasks.get(i);
		sb.append(i+1 + ". ");
		sb.append(task.getTaskName() + " [" + task.getTaskType() + "]");
		sb.append(" - " + task.getTaskStatus());
		sb.append(" | Priority: " + task.getPriority());

		if(task.getDueDate() != null) {
			long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, task.getDueDate());
			//sb.append(" ");
			if(daysUntil == 0) {
				sb.append(" (Due Today!)");
			} else if (daysUntil == 1) {
				sb.append(" (Due Tomorrow)");
			} else if (daysUntil > 0) {
				sb.append(" (Due in ").append(daysUntil).append(" days!)");
			} else {
				sb.append(" (Overdue by ").append(-daysUntil).append(" days!)");
			}
		}

		//Display course if any
		if(!task.getCourses().isEmpty()) {
			sb.append(" [Courses: ");
			for(int j = 0; j<task.getCourses().size(); j ++) {
				if(j > 0)  sb.append(", ");
				sb.append(task.getCourses().get(j).getName());
			}
			sb.append("]");
		}
		sb.append("\n");
	}
	System.out.println(sb.toString());
}

//VIEW UPCOMING TASKS THAT ARE PENDING OR INCOMPLETE
private void viewUpcomingIncompleteTasksUI(Scanner scanner) {
	if(this.tasks.isEmpty()) {
		System.out.println("No tasks to display!");
		return;
	}
	System.out.println("\nUpcoming Incomplete Tasks (Next 7 Days)");
	LocalDate today = LocalDate.now();
	LocalDate sevenDaysLater = today.plusDays(7);

	ArrayList<Task> upcomingIncompleteTasks = new ArrayList<>();
	for(Task task: this.tasks) {
		LocalDate dueDate = task.getDueDate();
		Task.Status status = task.getTaskStatus();
		if(dueDate != null && 
				(status == Task.Status.TODO || status == Task.Status.IN_PROGRESS) &&
				(dueDate.isEqual(today) || dueDate.isAfter(today)) &&
				(dueDate.isBefore(sevenDaysLater) || dueDate.isEqual(sevenDaysLater))) {

			upcomingIncompleteTasks.add(task);
		}
	}

	//Sort Tasks by dueDates and secondary sort by priority
	upcomingIncompleteTasks.sort((task1, task2) -> {
		int dateCompare = task1.getDueDate().compareTo(task2.getDueDate());
		if(dateCompare != 0) return dateCompare;
		return task1.getPriority().compareTo(task2.getPriority());
	});
	displayUpcomingIncompleteTasks(upcomingIncompleteTasks, today);
}

private void displayUpcomingIncompleteTasks(ArrayList<Task> tasks, LocalDate today) {
	if(tasks.isEmpty() ) {
		System.out.println("No upcoming incomplete tasks found!");
		return;
	}
	HashMap<LocalDate, ArrayList<Task>> tasksByDate = new HashMap<>();
	for(Task task: tasks) {
		LocalDate dueDate = task.getDueDate();             
		//create an arrayList for that date, if absent (e.g. : tasksByDate = { "2026-01-16":[Task1,Task2] , "2026-01-17":[Task3] }
		tasksByDate.putIfAbsent(dueDate, new ArrayList<>());
		tasksByDate.get(dueDate).add(task);
	}
	//sort dates; All tasks in one list, ordered by due date
	ArrayList<LocalDate> sortedDates = new ArrayList<>(tasksByDate.keySet());
	Collections.sort(sortedDates);
	
	for(LocalDate date: sortedDates) {
		
		String dataLabel = formatDateLabel(date, today);
		System.out.println("\n" + dataLabel + ":");
		ArrayList<Task> dateTasks = tasksByDate.get(date);
		
		for(int i = 0; i<dateTasks.size(); i++) {
			
			Task task = dateTasks.get(i);
			System.out.print(" " + (i+1) + ". ");
			System.out.print(task.getTaskName() + " [" + task.getTaskType() + "]");
			System.out.print(" - " + task.getTaskStatus());
			System.out.print(" | Priority: " + task.getPriority());

			//Show courses if any
			if(!task.getCourses().isEmpty()) {
				System.out.print(" [Courses: ");
				for(int j = 0; j < task.getCourses().size(); j++) {
					if(j>0) {
						System.out.print(", ");
					}
					System.out.print(task.getCourses().get(j).getName());
				}
				System.out.print("]");
			}
			System.out.println();
		}
	}
	System.out.println("\nFound " + tasks.size() + " upcoming incomplete task(s)");
}

private String formatDateLabel(LocalDate date, LocalDate today) {
	if(date.isEqual(today)) {
		return date.toString() + " (Today)";
	} else if (date.isEqual(today.plusDays(1))) {
		return date.toString() + " (Tomorrow)";
	} else {
		long daysUntil = ChronoUnit.DAYS.between(today, date);
		return date.toString() + " (In " + daysUntil + " days)";
	}
}

//DELETING TASK
private void deleteTaskUI(Scanner scanner) {
	if(this.tasks.isEmpty()) {
		System.out.print("No tasks available to delete");
		return;
	}
	System.out.println("\nAvailable Tasks:");
	System.out.print(displayAllTasks());
	System.out.println("Select task number to delete (1-" + tasks.size() + ") or 0 to cancel: ");
	int choice = getValidChoice(scanner, 0, tasks.size());
	if(choice == 0) {
		System.out.println("Delete cancelled.");
		return;
	}

	Task taskToDelete = tasks.get(choice-1);
	System.out.println("\nTask to delete:");
	System.out.println(taskToDelete.toString());

	//Confirm
	System.out.println("Are you sure? (y/n): ");
	if(!getYesOrNoConfirmation(scanner)) {
		System.out.println("Delete cancelled.");
		return;			
	}

	//Remove task from ALL linked courses first
	//use copy to avoid concurrentModificationException
	ArrayList<Course> linkedCourses = new ArrayList<>(taskToDelete.getCourses());
	for(Course course: linkedCourses) {
		taskToDelete.removeCourse(course);
	}
	//Remove from Study Planner's task list
	boolean removed = tasks.remove(taskToDelete);
	if(removed) {
		System.out.println("Task deleted successfully.");
	} else {
		System.out.println("Failed to delete task.");
	}
}

//Helper methods for delete task
private int getValidChoice(Scanner scanner, int min, int max) {
	while(true) {
		try {
			String input = scanner.nextLine().trim();
			int choice = Integer.parseInt(input);
			if (choice >= min && choice <= max) {
				return choice;
			} else {
				System.out.println("Invalid choice. Please enter " + min + "-" + max + ": ");
			}
		} catch (NumberFormatException e) {
			System.out.println("Please enter a valid number: ");
		}
	}
}

private boolean getYesOrNoConfirmation(Scanner scanner) {
	while(true) {
		String input = scanner.nextLine().trim();
		if(input.equals("y") || input.equals("yes")) {
			return true;
		}
		else if(input.equals("n") || input.equals("no")) {
			return false;
		}
		else {
			System.out.println("Please enter 'y' or 'n': ");
		}
	}
}

//DELETE COURSE
private void deleteCourseUI(Scanner scanner) {
	if(this.courses.isEmpty()) {
		System.out.print("No courses available to delete");
		return;
	}
	System.out.println("\nAvailable Courses: ");
	System.out.print(displayCourses());
	System.out.println("Select course number to delete (1-" + courses.size() + ") or 0 to cancel: ");
	int choice = getValidChoice(scanner, 0, courses.size());
	if(choice == 0) {
		System.out.println("Delete cancelled.");
		return;
	}

	Course courseToDelete = courses.get(choice-1);
	System.out.println("\nCourse to delete:");
	System.out.println(courseToDelete.toString());

	//Confirm
	System.out.println("Are you sure? (y/n): ");
	if(!getYesOrNoConfirmation(scanner)) {
		System.out.println("Delete cancelled.");
		return;			
	}

	//Remove course from ALL linked tasks first
	//use copy to avoid concurrentModificationException
	ArrayList<Task> linkedTasks = new ArrayList<>(courseToDelete.getTasks());
	for(Task task: linkedTasks) {
		//courseToDelete.removeTask(task);
		task.removeCourse(courseToDelete);
	}
	//Remove from Study Planner's course list
	boolean removed = courses.remove(courseToDelete);
	if(removed) {
		System.out.println("Course deleted successfully.");
	} else {
		System.out.println("Failed to delete course.");
	}
}

//SAVE DATA UI
private void saveDataUI(Scanner scanner) {
	System.out.println("\nSave Data");
	System.out.println("File will be saved to: " + DATA_FILE);
	System.out.println("Save data? (y/n): ");

	if(getYesOrNoConfirmation(scanner)) {
		FileManager.saveData(this, DATA_FILE);
	} else {
		System.out.println("Save cancelled.");
	}
}

//	//LOAD DATA UI
//	private void loadDataUI(Scanner scanner) {
//		System.out.println("\nLoad Data");
//		System.out.println("Warning: This will replace all current data!");
//		System.out.println("Load Data? (y/n): ");
//		
//		if(getYesOrNoConfirmation(scanner)) {
//			StudyPlanner loaded = FileManager.loadData(DATA_FILE);
//			//Replace current data with loaded data
//			this.replaceData(loaded);
//			System.out.println("Data loaded successfully!");
//		} else {
//			System.out.println("Load cancelled.");
//		}
//	}

private void replaceData(StudyPlanner loaded) {
	this.courses = new ArrayList<>(loaded.getAllCourses());
	this.tasks = new ArrayList<>(loaded.getAllTasks());
}

//SAVE DATA ON EXIT
private void saveDataOnExit() {
	System.out.println("\nSaving data before exit...");
	FileManager.saveData(this, DATA_FILE);
	System.out.println("Data saved successfully!");
}

//EDIT TASK
private void editTaskUI(Scanner scanner) {
	if(this.tasks.isEmpty()) {
		System.out.println("No tasks available to edit.");
		return;
	}
	System.out.println("Edit a Task");
	System.out.println("Current Tasks:");
	for(int i = 0; i<this.tasks.size(); i++) {
		Task task = this.tasks.get(i);
		System.out.printf("%d %s (Type: %s, Due %s, Status: %s)%n", i+1, task.getTaskName(), task.getTaskType(), task.getDueDate(), task.getTaskStatus());
	}
	System.out.print("\nEnter task name to edit: ");
	String taskName = scanner.nextLine().trim();

	Task task = findTaskByName(taskName);
	if (task == null) {
		System.out.println("Task not found!");
		return;
	}
	// Display current values
	System.out.println("\nCurrent Task Details:");
	System.out.println("Name: " + task.getTaskName());
	System.out.println("Type: " + task.getTaskType());
	System.out.println("Due Date: " + task.getDueDate());
	System.out.println("Status: " + task.getTaskStatus());
	System.out.println("Courses: " + task.getCourses().stream()
			.map(Course::getCode)
			.collect(java.util.stream.Collectors.joining(", ")));
	System.out.print("New Task Name [" + task.getTaskName() + "]:");
	String newName = scanner.nextLine().trim();
	if(newName.isEmpty()) newName = null;

	LocalDate newDueDate = null;
	while(true) {
		System.out.println("New Due Date (YYYY-MM-DD) [" + task.getDueDate() + "]");
		String dueDateStr = scanner.nextLine().trim();
		if(dueDateStr.isEmpty()) break; //keep current
		try {
			newDueDate = LocalDate.parse(dueDateStr);
			break;
		} catch (Exception e) {
			System.out.println("Invalid date format. Please use YYYY-MM-DD");
		}
	}

	Task.Status newStatus = null;
	while(true) {
		System.out.println("New Status (TODO/IN_PROGRESS/DONE) [" + task.getTaskStatus() + "]" );
		String statusStr = scanner.nextLine().trim().toUpperCase();
		if(statusStr.isEmpty()) break; //keep current
		try {
			newStatus = Task.Status.valueOf(statusStr);
			break;
		} catch(IllegalArgumentException e) {
			System.out.println("Invalid Status. Please enter TODO, IN_PROGRESS, or DONE");
		}
	}

	System.out.println("New Task Type [" + task.getTaskType() + "]");
	String newTaskType = scanner.nextLine().trim();
	if(newTaskType.isEmpty()) newTaskType = null;

	System.out.println("\nEdit Priority (press Enter to skip)");
	System.out.print("Do you want to change priority? (y/n): ");
	String changePriority = scanner.nextLine().trim().toLowerCase();

	Task.Priority newPriority = null;
	if (changePriority.equals("y") || changePriority.equals("yes")) {
		newPriority = selectPriorityUI(scanner);
		System.out.println("Priority changed to: " + newPriority);
	} else {
		System.out.println("Keeping current priority: " + task.getPriority());
	}

	//call method to edit
	boolean success = editTask(taskName, newName, newDueDate, newStatus, newTaskType, newPriority);
	if(success) {
		System.out.println("\nTask updated successfully!");
	} else {
		System.out.println("\nFailed to update task.");
	}
}

public boolean editTask(String taskName, String newName, LocalDate newDueDate, Task.Status newStatus, String newTaskType, Priority newPriority) {
	Task task = findTaskByName(taskName);
	if(task == null) {
		return false;
	}
	try {
		if(newName != null) {
			task.setName(newName);
		}
		if (newDueDate != null) {
			task.setDueDate(newDueDate);
		}
		if (newStatus != null) {
			task.setStatus(newStatus);
		}
		if (newTaskType != null && !newTaskType.trim().isEmpty()) {
			task.setTaskType(newTaskType);
		}
		if (newPriority != null) {
			task.setPriority(newPriority);
		}
		return true;
	} catch (IllegalArgumentException e) {
		return false;
	}		
}

public Task findTaskByName(String taskName) {
	if(taskName == null) return null;
	else {
		return tasks.stream()
				.filter(t -> t.getTaskName().equalsIgnoreCase(taskName.trim()))
				.findFirst()
				.orElse(null);					
	}
}

//EDIT COURSE
private void editCourseUI(Scanner scanner) {
	if(this.courses.isEmpty()) {
		System.out.println("No courses avaialble to edit.");
		return;
	}
	System.out.println("Current Courses:");
	System.out.println(displayCourses());
	System.out.println("\nEnter course code to edit: ");
	String courseCode = scanner.nextLine().trim().toUpperCase();

	Course course = findCourseByCode(courseCode);
	if(course == null) {
		System.out.println("Course not found!");
		return;
	}

	System.out.println("\nCurrent course details:");
	System.out.println("Name: " + course.getName());
	System.out.println("Code: " + course.getCode());
	System.out.println("Tasks: " + course.getTasks().size() + " task(s)");
	System.out.println("\nEnter new values (press Enter to keep current):");

	//new Name
	System.out.println("New Course Name [" + course.getName() + "]");
	String newName = scanner.nextLine().trim();
	if(newName.isEmpty()) newName = null;


	//new Code
	System.out.print("New Course Code [" + course.getCode() + "]: ");
	String newCode = scanner.nextLine().trim().toUpperCase();
	if (newCode.isEmpty()) newCode = null;

	boolean success = editCourse(courseCode, newName, newCode);
	if(success) {
		System.out.println("\nCourse updated successfully!");
		if (newCode != null && !newCode.equals(courseCode)) {
			System.out.println("\nNote: Course code changed from " + courseCode + " to " + newCode);
			System.out.println("All task references have been automatically updated.");
		}
	} else {
		System.out.println("\nFailed to update course.");
	}

}

public boolean editCourse(String courseCode, String newName, String newCode) {
	Course course = findCourseByCode(courseCode);
	if(course == null) {
		return false;
	}
	try {
		if(newCode != null && !newCode.trim().isEmpty() && !newCode.trim().equalsIgnoreCase(courseCode)) {
			Course existing = findCourseByCode(newCode.trim().toUpperCase());
			if(existing != null) {
				return false;
			}
			course.setCode(newCode);
		}
		if(newName != null && !newName.trim().isEmpty()){
			course.setName(newName);
		}
		return true;
	} catch(IllegalArgumentException e) {
		return false;
	}
}
}