package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import studyPlanner.model.Badge;
import studyPlanner.model.User;
import studyPlanner.repository.BadgeRepository;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.TaskRepository;
import java.util.List;

@Service
public class BadgeService {
    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    // call this after every action to check and award badges
    public void checkAndAwardBadges(User user) {
        long totalTasks = taskRepository.countDoneTasksByUser(user);
        long totalCourses = courseRepository.findByUser(user).size();

        // Getting started
        if (totalTasks >= 1)
            award(user, Badge.BadgeType.FIRST_TASK);
        if (totalCourses >= 1)
            award(user, Badge.BadgeType.FIRST_COURSE);

        // Volume
        if (totalTasks >= 5)
            award(user, Badge.BadgeType.TASKS_5);
        if (totalTasks >= 25)
            award(user, Badge.BadgeType.TASKS_25);
        if (totalTasks >= 100)
            award(user, Badge.BadgeType.TASKS_100);

        // Courses
        if (totalCourses >= 3)
            award(user, Badge.BadgeType.COURSES_3);
        if (totalCourses >= 5)
            award(user, Badge.BadgeType.COURSES_5);

        // Streak
        if (user.getStreakCount() >= 3)
            award(user, Badge.BadgeType.STREAK_3);
        if (user.getStreakCount() >= 7)
            award(user, Badge.BadgeType.STREAK_7);
        if (user.getStreakCount() >= 30)
            award(user, Badge.BadgeType.STREAK_30);

        // XP milestones
        if (user.getXpTotal() >= 100)
            award(user, Badge.BadgeType.XP_100);
        if (user.getXpTotal() >= 500)
            award(user, Badge.BadgeType.XP_500);
        if (user.getXpTotal() >= 1000)
            award(user, Badge.BadgeType.XP_1000);
    }

    // award comeback badge when new streak starts after a previous loss
    public void checkComeback(User user) {
        if (user.getStreakCount() == 1 &&
                badgeRepository.existsByUserAndBadgeType(user, Badge.BadgeType.STREAK_3)) {
            award(user, Badge.BadgeType.COMEBACK);
        }
    }

    // get all badges for a user
    public List<Badge> getBadges(User user) {
        return badgeRepository.findByUser(user);
    }

    // award a badge only if not already earned
    private void award(User user, Badge.BadgeType type) {
        if (!badgeRepository.existsByUserAndBadgeType(user, type)) {
            badgeRepository.save(new Badge(user, type));
        }
    }
}
