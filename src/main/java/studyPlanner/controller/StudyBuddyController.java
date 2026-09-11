package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.dto.BuddyProfileDTO;
import studyPlanner.dto.StudyBuddyDTO;
import studyPlanner.service.StudyBuddyService;
import java.util.List;

@RestController
@RequestMapping("/api/study-buddies")
public class StudyBuddyController {

    @Autowired
    private StudyBuddyService studyBuddyService;

    @PostMapping("/{userId}")
    public ResponseEntity<StudyBuddyDTO> follow(@PathVariable Long userId) {
        StudyBuddyDTO result = studyBuddyService.followUser(userId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(201).body(result);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId) {
        boolean result = studyBuddyService.unfollowUser(userId);
        if (result)
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/following")
    public ResponseEntity<List<StudyBuddyDTO>> getFollowing() {
        return ResponseEntity.ok(studyBuddyService.getFollowing());
    }

    @GetMapping("/followers")
    public ResponseEntity<List<StudyBuddyDTO>> getFollowers() {
        return ResponseEntity.ok(studyBuddyService.getFollowers());
    }

    @GetMapping("/requests")
    public ResponseEntity<List<StudyBuddyDTO>> getPendingRequests() {
        return ResponseEntity.ok(studyBuddyService.getPendingRequests());
    }

    @PutMapping("/requests/{connectionId}/accept")
    public ResponseEntity<StudyBuddyDTO> acceptRequest(@PathVariable Long connectionId) {
        StudyBuddyDTO result = studyBuddyService.acceptRequest(connectionId);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/requests/{connectionId}")
    public ResponseEntity<Void> declineRequest(@PathVariable Long connectionId) {
        boolean result = studyBuddyService.declineRequest(connectionId);
        if (result)
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyBuddyDTO>> search(@RequestParam String q) {
        return ResponseEntity.ok(studyBuddyService.searchUsers(q));
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<BuddyProfileDTO> getProfile(@PathVariable Long userId) {
        BuddyProfileDTO result = studyBuddyService.getProfile(userId);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }
}
