package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum ActionType {
        TASK_COMPLETED,
        TASK_ADDED,
        COURSE_ADDED,
        COURSE_COMPLETED,
        STREAK_ACHIEVED,
        STREAK_STARTED,
        BADGE_EARNED,
        POMODORO_COMPLETED,
        GROUP_JOINED,
        GROUP_CREATED,
        POST_SHARED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Integer xpEarned;

    // Constructor
    public ActivityLog() {
    }

    public ActivityLog(User user, ActionType actionType, String description, Integer xpEarned) {
        this.user = user;
        this.actionType = actionType;
        this.description = description;
        this.xpEarned = xpEarned;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getXpEarned() {
        return xpEarned;
    }

    // Setters
    public void setUser(User user) {
        this.user = user;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }
}