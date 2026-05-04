package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import studyPlanner.model.ActivityLog;
import studyPlanner.model.User;
import studyPlanner.repository.ActivityLogRepository;
import studyPlanner.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityLog>> getRecentActivity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ActivityLog> activities = activityLogRepository
                .findTop10ByUserOrderByCreatedAtDesc(user);
        return ResponseEntity.ok(activities);
    }
}