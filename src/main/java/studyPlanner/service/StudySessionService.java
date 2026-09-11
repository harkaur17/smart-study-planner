package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.dto.BuddyStudyingDTO;
import studyPlanner.model.Course;
import studyPlanner.model.CourseBlock;
import studyPlanner.model.StudyBuddyConnection;
import studyPlanner.model.StudySession;
import studyPlanner.model.Task;
import studyPlanner.model.User;
import studyPlanner.repository.CourseRepository;
import studyPlanner.repository.JoinRequestRepository;
import studyPlanner.repository.StudyBuddyConnectionRepository;
import studyPlanner.repository.StudySessionRepository;
import studyPlanner.repository.TaskRepository;
import studyPlanner.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudySessionService {

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudyBuddyConnectionRepository studyBuddyConnectionRepository;

    @Autowired
    private JoinRequestRepository joinRequestRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // start a new session
    public StudySession startSession(List<Long> courseIds, Long taskId, List<Long> blockCourseIds,
            List<Integer> blockCounts, StudySession.Mode mode, int plannedSessions, int focusMinutes,
            int breakMinutes, boolean skipBreaks, boolean shareWithBuddies) {
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

        Long resolvedTaskId = null;
        String resolvedTaskName = null;
        if (taskId != null) {
            Optional<Task> task = taskRepository.findById(taskId);
            if (task.isPresent() && task.get().getUser().getId().equals(user.getId())) {
                resolvedTaskId = task.get().getId();
                resolvedTaskName = task.get().getTaskName();
            }
        }

        List<CourseBlock> courseBlocks = new ArrayList<>();
        int resolvedPlannedSessions = plannedSessions;
        if (blockCourseIds != null && !blockCourseIds.isEmpty()) {
            int total = 0;
            for (int i = 0; i < blockCourseIds.size(); i++) {
                Long blockCourseId = blockCourseIds.get(i);
                Integer count = blockCounts.get(i);
                if (blockCourseId == null || count == null || count < 1) continue;
                Optional<Course> course = courseRepository.findById(blockCourseId);
                if (course.isPresent() && course.get().getUser().getId().equals(user.getId())) {
                    courseBlocks.add(new CourseBlock(course.get().getId(), course.get().getCode(), count));
                    total += count;
                }
            }
            if (!courseBlocks.isEmpty()) resolvedPlannedSessions = total;
        }

        StudySession session = new StudySession(user, courses, mode, resolvedPlannedSessions,
                focusMinutes, breakMinutes, skipBreaks, resolvedTaskId, resolvedTaskName, courseBlocks,
                shareWithBuddies);
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
        StudySession saved = studySessionRepository.save(session);
        joinRequestRepository.deleteBySession(saved);
        return saved;
    }

    // session history for this user
    public List<StudySession> getHistory() {
        User user = getCurrentUser();
        return studySessionRepository.findByUserOrderByStartedAtDesc(user);
    }

    // buddies (accepted, one-way follows) who are currently sharing an active session
    public List<BuddyStudyingDTO> getBuddiesStudyingNow() {
        User user = getCurrentUser();
        List<User> following = studyBuddyConnectionRepository
                .findByFollowerAndStatus(user, StudyBuddyConnection.Status.ACCEPTED)
                .stream()
                .map(StudyBuddyConnection::getFollowing)
                .collect(Collectors.toList());
        if (following.isEmpty()) return new ArrayList<>();

        return studySessionRepository.findByUserInAndEndedAtIsNullAndShareWithBuddiesTrue(following)
                .stream()
                .map(this::toBuddyStudyingDTO)
                .collect(Collectors.toList());
    }

    private BuddyStudyingDTO toBuddyStudyingDTO(StudySession session) {
        BuddyStudyingDTO dto = new BuddyStudyingDTO();
        dto.sessionId = session.getId();
        dto.userId = session.getUser().getId();
        dto.name = session.getUser().getName();
        dto.username = session.getUser().getUsername();
        dto.startedAt = session.getStartedAt();
        if (!session.getCourses().isEmpty()) {
            String firstCode = session.getCourses().get(0).getCode();
            int extra = session.getCourses().size() - 1;
            dto.courseLabel = extra > 0 ? firstCode + " +" + extra : firstCode;
        }
        return dto;
    }
}