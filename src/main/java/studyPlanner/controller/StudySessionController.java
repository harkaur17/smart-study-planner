package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.model.StudySession;
import studyPlanner.service.StudySessionService;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/study-sessions")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

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
                request.focusMinutes, request.breakMinutes, request.skipBreaks);
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
}