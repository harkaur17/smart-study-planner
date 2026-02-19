package studyPlanner.persistence;

import java.util.List;
import java.util.ArrayList;

public class StudyPlannerDTO {

	private List<CourseDTO> courses;
	private List<TaskDTO> tasks;

	public StudyPlannerDTO() {
		this.courses = new ArrayList<>();
		this.tasks = new ArrayList<>();
	}

	public StudyPlannerDTO(List<CourseDTO> courses, List<TaskDTO> tasks) {
		if(courses == null) {
			this.courses = new ArrayList<>();
		} else {
			this.courses = new ArrayList<>(courses);
		}

		if(tasks == null) {
			this.tasks = new ArrayList<>();
		} else {
			this.tasks = new ArrayList<>(tasks);
		}
	}

	//GETTERS
	public List<CourseDTO> getCourses(){
		return new ArrayList<>(courses);
	}

	public List<TaskDTO> getTasks(){
		return new ArrayList<>(tasks);
	}
	
	public int getCourseCount() {
		return this.courses.size();
	}

	public int getTaskCount() {
		return this.tasks.size();
	}


	//SETTERS
	public void setCourses(List<CourseDTO> courses) {
		if(courses == null) {
			this.courses = new ArrayList<>();
		} else {
			this.courses = new ArrayList<>(courses);
		}
	}

	public void setTasks(List<TaskDTO> tasks) {
		if(tasks == null) {
			this.tasks = new ArrayList<>();
		} else {
			this.tasks = new ArrayList<>(tasks);
		}
	}

	public void addTask(TaskDTO task) {
		if (task != null) {
			this.tasks.add(task);
		}
	}

	public void addCourse(CourseDTO course) {
		if(course != null) {
			this.courses.add(course);
		}
	}

	public void clear() {
		this.tasks.clear();
		this.courses.clear();
	}

	@Override
	public String toString() {
		return String.format("StudyPlannerDTO{courses=%d, tasks=%d}", 
				courses.size(), tasks.size());
	}
}
