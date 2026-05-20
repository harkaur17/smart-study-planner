package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "badges")
public class Badge {
    public enum BadgeType {
        // Getting started
        FIRST_TASK,
        FIRST_COURSE,
        FIRST_CALENDAR_TASK,

        // Consistency
        STREAK_3,
        STREAK_7,
        STREAK_30,

        // Volume
        TASKS_5,
        TASKS_25,
        TASKS_100,

        // Courses
        COURSES_3,
        COURSES_5,

        // XP milestones
        XP_100,
        XP_500,
        XP_1000,

        // Comeback
        COMEBACK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType badgeType;

    @Column(nullable = false)
    private LocalDate earnedAt;

    public Badge() {
    }

    public Badge(User user, BadgeType badgeType) {
        this.user = user;
        this.badgeType = badgeType;
        this.earnedAt = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public LocalDate getEarnedAt() {
        return earnedAt;
    }
}
