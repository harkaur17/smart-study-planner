package studyPlanner.model;

import java.util.ArrayList;
import java.time.LocalDate;

public class Task {

	private String taskName;
	private String taskType;
	private LocalDate dueDate;

	private ArrayList<Course> courses;

	//enum for status of the task
	public enum Status {
		TODO,
		IN_PROGRESS,
		DONE
	}
	private Status status;

	public enum Priority{
		HIGH,
		MEDIUM,
		LOW
	}
	private Priority priority;

	//constructor #1 for tasks with all the attributes, except priority
	public Task(String taskName, String taskType, LocalDate dueDate, Status status) {

		if(taskName == null || taskName.trim().isEmpty()) {
			throw new IllegalArgumentException("Task name cannot be null.");
		}
		if(taskType == null || taskType.trim().isEmpty()) {
			throw new IllegalArgumentException("Task type cannot be null.");
		}
		if(status == null) {
			throw new IllegalArgumentException("Task Status cannot be null.");
		}
		this.taskName = taskName;
		this.dueDate = dueDate;
		this.taskType = taskType;
		this.status = status;
		this.courses = new ArrayList<Course>();
		this.priority = Priority.MEDIUM; //default option set to MEDIUM
	}

	//constructor #2 for tasks with no due date
	public Task(String taskName, String taskType, Status status) {

		if(taskName == null  || taskName.trim().isEmpty()) {
			throw new IllegalArgumentException("Task name cannot be null.");
		}
		if(taskType == null || taskType.trim().isEmpty()) {
			throw new IllegalArgumentException("Task type cannot be null.");
		}
		if(status == null) {
			throw new IllegalArgumentException("Task Status cannot be null.");
		}
		this.taskName = taskName;
		this.taskType = taskType;
		this.status = status;
		this.dueDate = null;
		this.courses = new ArrayList<Course>();
		this.priority = Priority.MEDIUM; //default option set to MEDIUM
	}

	//constructor #3 for tasks with all the attributes
	public Task(String taskName, String taskType, LocalDate dueDate, Status status, Priority priority) {

		if(taskName == null || taskName.trim().isEmpty()) {
			throw new IllegalArgumentException("Task name cannot be null.");
		}
		if(taskType == null || taskType.trim().isEmpty()) {
			throw new IllegalArgumentException("Task type cannot be null.");
		}
		if(status == null) {
			throw new IllegalArgumentException("Task Status cannot be null.");
		}
		this.taskName = taskName;
		this.dueDate = dueDate;
		this.taskType = taskType;
		this.status = status;
		this.courses = new ArrayList<Course>();
		this.priority = priority;
	}

	//constructor #4 for tasks with priority and no due date
	public Task(String taskName, String taskType, Status status, Priority priority) {

		if(taskName == null || taskName.trim().isEmpty()) {
			throw new IllegalArgumentException("Task name cannot be null.");
		}
		if(taskType == null || taskType.trim().isEmpty()) {
			throw new IllegalArgumentException("Task type cannot be null.");
		}
		if(status == null) {
			throw new IllegalArgumentException("Task Status cannot be null.");
		}
		this.taskName = taskName;
		this.dueDate = null;
		this.taskType = taskType;
		this.status = status;
		this.courses = new ArrayList<Course>();
		this.priority = priority;
	}

	public void addCourse(Course c) {
		if(c==null) return;
		if(!this.courses.contains(c)) {
			this.courses.add(c);
			c.addTask(this);
		}
	}

	public void setDueDate(LocalDate dueDate) {
		//Not needed?
		//TODO: later update this so that if the dueDate is from past then throw exception
		if(dueDate == null) {
			throw new IllegalArgumentException("Task Status cannot be null.");
		}
		this.dueDate = dueDate;
	}

	//GETTERS

	public String getTaskName() {
		return this.taskName;
	}

	public Status getTaskStatus() {
		return this.status;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public LocalDate getDueDate() {
		return this.dueDate;
	}

	public int getCourseCount() {
		return this.courses.size();
	}

	public ArrayList<Course> getCourses(){
		return new ArrayList<Course>(this.courses);
	}

	public Priority getPriority() {
		return this.priority;
	}

	//SETTERS

	public void setName(String taskName) {
		if(taskName == null || taskName.trim().isEmpty()) {
			throw new IllegalArgumentException("Task name cannot be empty.");
		}
		this.taskName = taskName;
	}

	public void setTaskType(String taskType) {
		if (taskType == null || taskType.trim().isEmpty()) {
			throw new IllegalArgumentException("Task type cannot be empty.");
		}
		this.taskType = taskType.trim();
	}

	public void setStatus(Status status) {
		if(status == null) {
			throw new IllegalArgumentException("Status cannot be null.");
		}
		this.status = status;
	}

	public void setPriority(Priority priority) {
		if(priority == null) {
			throw new IllegalArgumentException("Priority cannot be null.");
		}
		this.priority = priority;
	}

	public boolean removeCourse(Course course) {
		if(course == null) {
			return false;
		}
		boolean removed = courses.remove(course);
		if(removed) {
			course.removeTask(this);
		}
		return removed;
	}

	public boolean removeCourseByCode(String courseCode) {

		if(courseCode == null) {
			return false;
		}
		courseCode = courseCode.trim().toUpperCase();
		for (Course c: this.courses) {
			if(c.getCode().equals(courseCode)) {
				boolean removed = this.courses.remove(c);
				if(removed) {
					c.removeTask(this);
				}
				return removed;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append(String.format("Task: %s\n", this.taskName));
		result.append(String.format("Task Type: %s\n", this.taskType));
		result.append(String.format("Status: %s\n", this.status));
		result.append(String.format("Priority: %s\n", this.priority));

		if (this.dueDate != null) {
			result.append(String.format("Due Date: %s\n", this.dueDate));
		}

		if (!this.courses.isEmpty()) {
			result.append("Courses:\n");
			for (int i = 0; i < this.courses.size(); i++) {
				result.append(String.format("  %d. %s\n", i + 1, this.courses.get(i)));
			}
		}

		return result.toString();
	}

}


