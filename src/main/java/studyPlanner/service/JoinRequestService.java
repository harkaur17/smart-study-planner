package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.dto.JoinRequestDTO;
import studyPlanner.model.JoinRequest;
import studyPlanner.model.StudyBuddyConnection;
import studyPlanner.model.StudySession;
import studyPlanner.model.User;
import studyPlanner.repository.JoinRequestRepository;
import studyPlanner.repository.StudyBuddyConnectionRepository;
import studyPlanner.repository.StudySessionRepository;
import studyPlanner.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JoinRequestService {

    @Autowired
    private JoinRequestRepository joinRequestRepository;

    @Autowired
    private StudySessionRepository studySessionRepository;

    @Autowired
    private StudyBuddyConnectionRepository studyBuddyConnectionRepository;

    @Autowired
    private UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ask to join a buddy's shared, active session
    public JoinRequestDTO askToJoin(Long sessionId) {
        User user = getCurrentUser();
        Optional<StudySession> optional = studySessionRepository.findById(sessionId);
        if (!optional.isPresent()) return null;
        StudySession session = optional.get();

        if (session.getEndedAt() != null) return null;
        if (!session.isShareWithBuddies()) return null;
        if (session.getUser().getId().equals(user.getId())) return null;

        Optional<StudyBuddyConnection> connection = studyBuddyConnectionRepository
                .findByFollowerAndFollowing(user, session.getUser());
        if (!connection.isPresent() || connection.get().getStatus() != StudyBuddyConnection.Status.ACCEPTED) {
            return null;
        }

        if (joinRequestRepository.findByRequesterAndSession(user, session).isPresent()) return null;

        JoinRequest request = new JoinRequest(user, session);
        joinRequestRepository.save(request);
        return toDTO(request);
    }

    // pending requests to join the current user's own active session
    public List<JoinRequestDTO> getPendingRequestsForMySession() {
        User user = getCurrentUser();
        Optional<StudySession> active = studySessionRepository.findFirstByUserAndEndedAtIsNull(user);
        if (!active.isPresent()) return new ArrayList<>();

        return joinRequestRepository.findBySessionAndStatus(active.get(), JoinRequest.Status.PENDING)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public JoinRequestDTO acceptRequest(Long requestId) {
        User user = getCurrentUser();
        Optional<JoinRequest> optional = joinRequestRepository.findById(requestId);
        if (!optional.isPresent()) return null;
        JoinRequest request = optional.get();
        if (!request.getSession().getUser().getId().equals(user.getId())) return null;
        if (request.getStatus() != JoinRequest.Status.PENDING) return null;

        request.setStatus(JoinRequest.Status.ACCEPTED);
        joinRequestRepository.save(request);
        return toDTO(request);
    }

    // either the session owner (declining) or the requester (cancelling) can remove a request
    public boolean declineRequest(Long requestId) {
        User user = getCurrentUser();
        Optional<JoinRequest> optional = joinRequestRepository.findById(requestId);
        if (!optional.isPresent()) return false;
        JoinRequest request = optional.get();

        boolean isSessionOwner = request.getSession().getUser().getId().equals(user.getId());
        boolean isRequester = request.getRequester().getId().equals(user.getId());
        if (!isSessionOwner && !isRequester) return false;

        joinRequestRepository.delete(request);
        return true;
    }

    private JoinRequestDTO toDTO(JoinRequest request) {
        JoinRequestDTO dto = new JoinRequestDTO();
        dto.id = request.getId();
        dto.requesterId = request.getRequester().getId();
        dto.requesterName = request.getRequester().getName();
        dto.requesterUsername = request.getRequester().getUsername();
        return dto;
    }
}
