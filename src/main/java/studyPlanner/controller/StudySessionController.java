package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.dto.BuddyStudyingDTO;
import studyPlanner.dto.JoinRequestDTO;
import studyPlanner.model.StudySession;
import studyPlanner.service.JoinRequestService;
import studyPlanner.service.StudySessionService;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/study-sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    @Autowired
    private JoinRequestService joinRequestService;

    static class CourseBlockRequest {
        public Long courseId;
        public int sessionCount;
    }

    static class StartSessionRequest {
        public List<Long> courseIds;
        public Long taskId;
        public List<CourseBlockRequest> courseBlocks;
        public String mode;
        public int plannedSessions;
        public int focusMinutes;
        public int breakMinutes;
        public boolean skipBreaks;
        public boolean shareWithBuddies;
    }

    @PostMapping
    public ResponseEntity<StudySession> startSession(@RequestBody StartSessionRequest request) {
        StudySession.Mode mode = StudySession.Mode.valueOf(request.mode);

        List<Long> blockCourseIds = new ArrayList<>();
        List<Integer> blockCounts = new ArrayList<>();
        if (request.courseBlocks != null) {
            for (CourseBlockRequest block : request.courseBlocks) {
                blockCourseIds.add(block.courseId);
                blockCounts.add(block.sessionCount);
            }
        }

        StudySession session = studySessionService.startSession(
                request.courseIds, request.taskId, blockCourseIds, blockCounts, mode, request.plannedSessions,
                request.focusMinutes, request.breakMinutes, request.skipBreaks, request.shareWithBuddies);
        if (session == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(201).body(session);
    }

    @GetMapping("/active")
    public ResponseEntity<StudySession> getActiveSession() {
        StudySession session = studySessionService.getActiveSession();
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<StudySession> incrementProgress(@PathVariable Long id) {
        StudySession session = studySessionService.incrementProgress(id);
        if (session == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(session);
    }

    @PutMapping("/{id}/end")
    public ResponseEntity<StudySession> endSession(@PathVariable Long id) {
        StudySession session = studySessionService.endSession(id);
        if (session == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(session);
    }

    @GetMapping("/history")
    public ResponseEntity<List<StudySession>> getHistory() {
        return ResponseEntity.ok(studySessionService.getHistory());
    }

    @GetMapping("/buddies-active")
    public ResponseEntity<List<BuddyStudyingDTO>> getBuddiesStudyingNow() {
        return ResponseEntity.ok(studySessionService.getBuddiesStudyingNow());
    }

    @PostMapping("/{sessionId}/join-requests")
    public ResponseEntity<JoinRequestDTO> askToJoin(@PathVariable Long sessionId) {
        JoinRequestDTO result = joinRequestService.askToJoin(sessionId);
        if (result == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(201).body(result);
    }

    @GetMapping("/active/join-requests")
    public ResponseEntity<List<JoinRequestDTO>> getPendingRequestsForMySession() {
        return ResponseEntity.ok(joinRequestService.getPendingRequestsForMySession());
    }

    @PutMapping("/join-requests/{requestId}/accept")
    public ResponseEntity<JoinRequestDTO> acceptJoinRequest(@PathVariable Long requestId) {
        JoinRequestDTO result = joinRequestService.acceptRequest(requestId);
        if (result == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/join-requests/{requestId}")
    public ResponseEntity<Void> declineJoinRequest(@PathVariable Long requestId) {
        boolean result = joinRequestService.declineRequest(requestId);
        if (result)
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}