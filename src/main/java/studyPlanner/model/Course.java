package studyPlanner.model;

import java.util.ArrayList;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String code;

	@ManyToMany
	@JoinTable(
		name = "course_tasks",
		joinColumns = @JoinColumn(name = "course_id"),
		inverseJoinColumns = @JoinColumn(name = "task_id")
	)
	private List<Task> tasks = new ArrayList<>();

	public Course(String name, String code) {
		//Validation
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Course name cannot be null or empty");
		}
		if (code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("Course code cannot be null or empty");
		}
		this.name = name;
		this.code = code;
		this.tasks = new ArrayList<Task>();
	}

	protected Course() {}

	public String getName() {
		return this.name;
	}

	public String getCode() {
		return this.code;
	}

	public Long getId(){
		return this.id;
	}
	
	public void setName(String name) {
		if(name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Course name cannot be null.");
		}
		this.name = name;
	}
	
	public void setCode(String code) {
		if(code == null || code.trim().isEmpty()) {
			throw new IllegalArgumentException("Course code cannot be null.");
		}
		this.code = code;
	}
	
	public void addTask(Task t) {
		if(t == null) return;
		if(!this.tasks.contains(t)) {
			this.tasks.add(t);
		}
	}
	
	public void removeTask(Task t) {
		if(t == null) return;
		this.tasks.remove(t);
	}
	
	public ArrayList<Task> getTasks(){
		return new ArrayList<Task>(this.tasks);
	}
	
	public String displayTasks() {
		if(this.tasks.isEmpty()) {
			return "No tasks assigned to this course.";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Tasks for %s (%s):\n",this.name, this.code));
		for(int i = 0; i<this.tasks.size(); i++) {
			Task task = this.tasks.get(i);
			sb.append(String.format("%d. %s - %s | Priority: %s", 
					i+1, 
					task.getTaskName(), 
					task.getTaskStatus(),
					task.getPriority()));
			
			if(task.getDueDate() != null) {
				sb.append(String.format(" (Due: %s)", task.getDueDate()));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public String toString() {
		return String.format("Course Name: %s, Code: %s", this.name, this.code);
	}

}
