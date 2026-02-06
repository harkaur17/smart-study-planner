package studyPlanner.persistence;

import java.util.List;
import java.util.ArrayList;

public class CourseDTO {
	
	private String name;
	private String code;
	private List<String> taskNames;

	//should the methods be public or private?
	public CourseDTO() {
		this.taskNames = new ArrayList<>();
	}
	
	public CourseDTO(String name, String code, List<String> taskNames) {
		this.name = name;
		this.code = code;
		if(taskNames == null) {
			this.taskNames = new ArrayList<>();
		} else {
			this.taskNames = new ArrayList<>(taskNames);
		}
	}
	
	//getters
	public String getName() {
		return this.name;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public List<String> getTaskNames(){
		return new ArrayList<>(taskNames);
	}
	
	//setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public void setTaskNames(List<String> taskNames) {
		this.taskNames = (taskNames != null) ? new ArrayList<>(taskNames) : new ArrayList<>();
	}
	
	public void addTaskName(String taskName) {
		if(taskName != null && !taskNames.contains(taskName)) {
			this.taskNames.add(taskName);
		}
	}
	
	public boolean removeTaskName(String taskName) {
		return taskNames.remove(taskName);
	}
}
