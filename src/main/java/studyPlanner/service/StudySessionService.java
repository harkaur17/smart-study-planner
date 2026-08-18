package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.model.Course;
import studyPlanner.model.StudySession;
import studyPlanner.model.User;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.StudySessionRepository;
import studyPlanner.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // start a new session
    public StudySession startSession(List<Long> courseIds, StudySession.Mode mode,
            int plannedSessions, int focusMinutes, int breakMinutes, boolean skipBreaks) {
        User user = getCurrentUser();

        // don't allow starting a new session while one is already active
        Optional<StudySession> existing = studySessionRepository.findFirstByUserAndEndedAtIsNull(user);
        if (existing.isPresent()) return null;

        List<Course> courses = new ArrayList<>();
        if (courseIds != null) {
            for (Long courseId : courseIds) {
                Optional<Course> course = courseRepository.findById(courseId);
                if (course.isPresent() && course.get().getUser().getId().equals(user.getId())) {
                    courses.add(course.get());
                }
            }
        }

        StudySession session = new StudySession(user, courses, mode, plannedSessions,
                focusMinutes, breakMinutes, skipBreaks);
        return studySessionRepository.save(session);
    }

    // get the current user's active (in-progress) session, if any
    public StudySession getActiveSession() {
        User user = getCurrentUser();
        return studySessionRepository.findFirstByUserAndEndedAtIsNull(user).orElse(null);
    }

    // increment completed focus blocks
    public StudySession incrementProgress(Long sessionId) {
        User user = getCurrentUser();
        Optional<StudySession> optional = studySessionRepository.findById(sessionId);
        if (!optional.isPresent()) return null;
        StudySession session = optional.get();
        if (!session.getUser().getId().equals(user.getId())) return null;
        session.setCompletedSessions(session.getCompletedSessions() + 1);
        return studySessionRepository.save(session);
    }

    // end a session
    public StudySession endSession(Long sessionId) {
        User user = getCurrentUser();
        Optional<StudySession> optional = studySessionRepository.findById(sessionId);
        if (!optional.isPresent()) return null;
        StudySession session = optional.get();
        if (!session.getUser().getId().equals(user.getId())) return null;
        session.setEndedAt(LocalDateTime.now());
        return studySessionRepository.save(session);
    }

    // session history for this user
    public List<StudySession> getHistory() {
        User user = getCurrentUser();
        return studySessionRepository.findByUserOrderByStartedAtDesc(user);
    }
}