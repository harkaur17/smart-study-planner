package studyPlanner.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "courses")
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String code;

	@Column
	private String semester;

	@Column
	private Integer year;

	@Column(nullable = false)
	private boolean isCompleted = false;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "course_tasks", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "task_id"))
	private List<Task> tasks = new ArrayList<>();

	@Column
	private String color;

	// Constructors
	public Course() {
	}

	public Course(String name, String code, User user) {
		this.name = name;
		this.code = code;
		this.user = user;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getSemester() {
		return semester;
	}

	public Integer getYear() {
		return year;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public User getUser() {
		return user;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public String getColor(){
		return color;
	}

	// Setters
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setSemester(String semester) {
		this.semester = semester;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addTask(Task task) {
		if (!this.tasks.contains(task)) {
			this.tasks.add(task);
		}
	}

	public void removeTask(Task task) {
		this.tasks.remove(task);
	}

	public void setColor(String color){
		this.color = color;
	}
}