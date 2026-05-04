package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String taskName;

	@Column(nullable = false)
	private String taskType;

	@Column
	private LocalDate dueDate;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonIgnore
	@ManyToMany(mappedBy = "tasks")
	private List<Course> courses = new ArrayList<>();

	public enum Status {
		TODO, IN_PROGRESS, DONE
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	public enum Priority {
		HIGH, MEDIUM, LOW
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Priority priority;

	// Constructors
	public Task() {
	}

	public Task(String taskName, String taskType, LocalDate dueDate, Status status, Priority priority, User user) {
		this.taskName = taskName;
		this.taskType = taskType;
		this.dueDate = dueDate;
		this.status = status;
		this.priority = priority;
		this.user = user;
	}

	public Task(String taskName, String taskType, Status status, Priority priority, User user) {
		this.taskName = taskName;
		this.taskType = taskType;
		this.status = status;
		this.priority = priority;
		this.user = user;
	}

	// Getters
	public Long getId() {
		return id;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getTaskType() {
		return taskType;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public User getUser() {
		return user;
	}

	public Status getTaskStatus() {
		return status;
	}

	public Priority getPriority() {
		return priority;
	}

	public List<Course> getCourses() {
		return new ArrayList<>(courses);
	}

	// Setters
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String taskName) {
		this.taskName = taskName;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public void addCourse(Course c) {
		if (c == null)
			return;
		if (!this.courses.contains(c)) {
			this.courses.add(c);
			c.addTask(this);
		}
	}

	public boolean removeCourse(Course course) {
		if (course == null)
			return false;
		boolean removed = courses.remove(course);
		if (removed) {
			course.removeTask(this);
		}
		return removed;
	}
}