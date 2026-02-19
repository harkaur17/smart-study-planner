package studyPlanner.persistence;

import java.util.List;
//import studyPlanner.model.Task.Status;
//import java.time.LocalDate;
import java.util.ArrayList;

public class TaskDTO {

	private String taskName;
	private String taskType;
	private String dueDate;
	private String status;
	private List<String> courseCodes;
	private String priority;
	//TODO: update dueDate and status data types -> later refinement 

	//Constructors
	public TaskDTO() {
		this.courseCodes = new ArrayList<>();
	}

	public TaskDTO(String taskName, String taskType, String dueDate, String status, String priority, List<String> courseCodes) {
		this.taskName = taskName;
		this.taskType = taskType;
		this.dueDate = dueDate;
		this.status = status;
		this.priority = priority;
		if(courseCodes == null) {
			this.courseCodes = new ArrayList<>();
		} else {
			this.courseCodes = new ArrayList<>(courseCodes);
		}	
	}

	//getters
	public String getTaskName() {
		return this.taskName;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public String getDueDate() {
		return this.dueDate;
	}

	public String getStatus() {
		return this.status;
	}
	
	public String getPriority() {
		return this.priority;
	}

	public List<String> getCourseCodes() { 
		return new ArrayList<>(courseCodes);
	}

	//setter
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}

	public void setCourseCodes(List<String> courseCodes) {
		this.courseCodes = (courseCodes != null) ? new ArrayList<>(courseCodes) : new ArrayList<>();
	}

	public void addCourseCode(String courseCode) {
		if(courseCode != null && !courseCodes.contains(courseCode)) {
			this.courseCodes.add(courseCode);
		}
	}

	public boolean removeCourseCode(String courseCode) {
		return this.courseCodes.remove(courseCode);
	}

	@Override
	public String toString() {
		return String.format("TaskDTO{taskName='%s', taskType='%s', dueDate='%s', status='%s', priority='%s', courseCodes=%s}",
				taskName, taskType, dueDate, status, priority, courseCodes);
	}
}
