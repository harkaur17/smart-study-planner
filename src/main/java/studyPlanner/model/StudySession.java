package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "session_courses", joinColumns = @JoinColumn(name = "session_id"), inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> courses = new ArrayList<>();

    public enum Mode {
        CLASSIC, FLOWTIME
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mode mode;

    @Column(nullable = false)
    private int plannedSessions;

    @Column(nullable = false)
    private int focusMinutes;

    @Column(nullable = false)
    private int breakMinutes;

    @Column(nullable = false)
    private boolean skipBreaks;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime endedAt;

    @Column(nullable = false)
    private int completedSessions = 0;

    @Column
    private Long taskId;

    @Column
    private String taskName;

    @ElementCollection
    @CollectionTable(name = "session_course_blocks", joinColumns = @JoinColumn(name = "session_id"))
    private List<CourseBlock> courseBlocks = new ArrayList<>();

    public StudySession() {
    }

    public StudySession(User user, List<Course> courses, Mode mode, int plannedSessions,
            int focusMinutes, int breakMinutes, boolean skipBreaks, Long taskId, String taskName,
            List<CourseBlock> courseBlocks) {
        this.user = user;
        this.courses = courses;
        this.mode = mode;
        this.plannedSessions = plannedSessions;
        this.focusMinutes = focusMinutes;
        this.breakMinutes = breakMinutes;
        this.skipBreaks = skipBreaks;
        this.taskId = taskId;
        this.taskName = taskName;
        this.courseBlocks = courseBlocks;
        this.startedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public Mode getMode() {
        return mode;
    }

    public int getPlannedSessions() {
        return plannedSessions;
    }

    public int getFocusMinutes() {
        return focusMinutes;
    }

    public int getBreakMinutes() {
        return breakMinutes;
    }

    public boolean isSkipBreaks() {
        return skipBreaks;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public int getCompletedSessions() {
        return completedSessions;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public List<CourseBlock> getCourseBlocks() {
        return courseBlocks;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setPlannedSessions(int plannedSessions) {
        this.plannedSessions = plannedSessions;
    }

    public void setFocusMinutes(int focusMinutes) {
        this.focusMinutes = focusMinutes;
    }

    public void setBreakMinutes(int breakMinutes) {
        this.breakMinutes = breakMinutes;
    }

    public void setSkipBreaks(boolean skipBreaks) {
        this.skipBreaks = skipBreaks;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public void setCompletedSessions(int completedSessions) {
        this.completedSessions = completedSessions;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setCourseBlocks(List<CourseBlock> courseBlocks) {
        this.courseBlocks = courseBlocks;
    }
}