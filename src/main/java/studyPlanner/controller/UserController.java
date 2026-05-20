package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import studyPlanner.model.User;
import studyPlanner.repository.UserRepository;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private studyPlanner.service.BadgeService badgeService;

    static class UpdateProfileRequest {
        public String school;
        public String program;
        public String yearLevel;
        public String username;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.school != null)
            user.setSchool(request.school);
        if (request.program != null)
            user.setProgram(request.program);
        if (request.yearLevel != null)
            user.setYearLevel(request.yearLevel);
        if (request.username != null && !request.username.trim().isEmpty()) {
            user.setUsername(request.username);
        }
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/badges")
    public ResponseEntity<List<studyPlanner.model.Badge>> getBadges() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(badgeService.getBadges(user));
    }
}