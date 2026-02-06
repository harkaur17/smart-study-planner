package studyPlanner.model;

import java.util.ArrayList;
import java.time.LocalDate;

public class Task {
	
	private String taskName;
	private String taskType;
	private LocalDate dueDate;
	
	// TODO: Upgrade to LocalDate (LATER)
    // private LocalDate dueLocalDate;
	// also add time attribute with dueDate
	//private String dueDate; changed to LocalDate
	private ArrayList<Course> courses;
	
	//using enum for status of the task
	public enum Status {
        TODO,
        IN_PROGRESS,
        DONE
    }
	private Status status;
	
	//constructor #1 for tasks with all the attributes
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
	}
	
	//constructor #2 for tasks with no due date
	public Task(String taskName, String taskType, Status status) {
		
		if(taskName == null) {
			throw new IllegalArgumentException("Task name cannot be null.");
		}
		if(taskType == null) {
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
	}
	
	public void addCourse(Course c) {
		if(c==null) return;
		if(!this.courses.contains(c)) {
			this.courses.add(c);
			c.addTask(this);
		}
	}
	
	//need to be fixed to update status only for the required course
	public void updateStatus(Status newStatus) {
		this.status = newStatus;
	}
	
	public void updateDueDate(LocalDate dueDate) {
		//later update this so that if the dueDate is from past then throw exception
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
		//return this.courses;
		return new ArrayList<Course>(this.courses);
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
	
//	public void delete() {
//		//Remove this task from all linked courses
//		ArrayList<Course> coursesCopy = new ArrayList<>(this.courses);
//		for(Course course: coursesCopy) {
//			this.removeCourse(course);
//		}
//		this.courses.clear();
//	}
	
	public String toString() {
		String result = "";
		result += String.format("Task: %s\nTask Type: %s\n",this.taskName, this.taskType);
		if(this.dueDate != null) {
			result += String.format("Due Date: %s\n", this.dueDate.toString());
		}
		if(this.courses.size() != 0) {
			result += "Courses: \n";
			for(int i = 0; i<this.courses.size(); i++) {
				result += String.format("  %d. %s\n", i+1, this.courses.get(i).toString());
			}
		}
		return result;
	}
}


