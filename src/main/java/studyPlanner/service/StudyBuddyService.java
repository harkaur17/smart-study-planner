package studyPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import studyPlanner.dto.BuddyProfileDTO;
import studyPlanner.dto.StudyBuddyDTO;
import studyPlanner.model.StudyBuddyConnection;
import studyPlanner.model.User;
import studyPlanner.repository.StudyBuddyConnectionRepository;
import studyPlanner.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudyBuddyService {

    @Autowired
    private StudyBuddyConnectionRepository studyBuddyConnectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgeService badgeService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public StudyBuddyDTO followUser(Long targetUserId) {
        User currentUser = getCurrentUser();
        if (targetUserId.equals(currentUser.getId())) return null;

        Optional<User> target = userRepository.findById(targetUserId);
        if (!target.isPresent()) return null;

        Optional<StudyBuddyConnection> existing =
                studyBuddyConnectionRepository.findByFollowerAndFollowing(currentUser, target.get());
        if (existing.isPresent()) return null;

        StudyBuddyConnection.Status status =
                target.get().isPublic() ? StudyBuddyConnection.Status.ACCEPTED : StudyBuddyConnection.Status.PENDING;
        StudyBuddyConnection connection = new StudyBuddyConnection(currentUser, target.get(), status);
        studyBuddyConnectionRepository.save(connection);

        return toDTO(target.get(), status.name());
    }

    public boolean unfollowUser(Long targetUserId) {
        User currentUser = getCurrentUser();
        Optional<User> target = userRepository.findById(targetUserId);
        if (!target.isPresent()) return false;

        Optional<StudyBuddyConnection> connection =
                studyBuddyConnectionRepository.findByFollowerAndFollowing(currentUser, target.get());
        if (!connection.isPresent()) return false;

        studyBuddyConnectionRepository.delete(connection.get());
        return true;
    }

    public List<StudyBuddyDTO> getFollowing() {
        User currentUser = getCurrentUser();
        return studyBuddyConnectionRepository
                .findByFollowerAndStatus(currentUser, StudyBuddyConnection.Status.ACCEPTED)
                .stream()
                .map(c -> toDTO(c.getFollowing(), "FOLLOWING"))
                .collect(Collectors.toList());
    }

    public List<StudyBuddyDTO> getFollowers() {
        User currentUser = getCurrentUser();
        return studyBuddyConnectionRepository
                .findByFollowingAndStatus(currentUser, StudyBuddyConnection.Status.ACCEPTED)
                .stream()
                .map(c -> {
                    User follower = c.getFollower();
                    Optional<StudyBuddyConnection> reverse =
                            studyBuddyConnectionRepository.findByFollowerAndFollowing(currentUser, follower);
                    String relationshipStatus = reverse.isPresent() ? reverse.get().getStatus().name() : "NONE";
                    return toDTO(follower, relationshipStatus);
                })
                .collect(Collectors.toList());
    }

    public List<StudyBuddyDTO> getPendingRequests() {
        User currentUser = getCurrentUser();
        return studyBuddyConnectionRepository
                .findByFollowingAndStatus(currentUser, StudyBuddyConnection.Status.PENDING)
                .stream()
                .map(c -> {
                    StudyBuddyDTO dto = toDTO(c.getFollower(), "PENDING");
                    dto.connectionId = c.getId();
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public StudyBuddyDTO acceptRequest(Long connectionId) {
        User currentUser = getCurrentUser();
        Optional<StudyBuddyConnection> optional = studyBuddyConnectionRepository.findById(connectionId);
        if (!optional.isPresent()) return null;

        StudyBuddyConnection connection = optional.get();
        if (!connection.getFollowing().getId().equals(currentUser.getId())) return null;
        if (connection.getStatus() != StudyBuddyConnection.Status.PENDING) return null;

        connection.setStatus(StudyBuddyConnection.Status.ACCEPTED);
        studyBuddyConnectionRepository.save(connection);
        return toDTO(connection.getFollower(), "FOLLOWING");
    }

    public boolean declineRequest(Long connectionId) {
        User currentUser = getCurrentUser();
        Optional<StudyBuddyConnection> optional = studyBuddyConnectionRepository.findById(connectionId);
        if (!optional.isPresent()) return false;

        StudyBuddyConnection connection = optional.get();
        if (!connection.getFollowing().getId().equals(currentUser.getId())) return false;

        studyBuddyConnectionRepository.delete(connection);
        return true;
    }

    public List<StudyBuddyDTO> searchUsers(String query) {
        User currentUser = getCurrentUser();
        return userRepository.findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(query, query)
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .map(u -> {
                    Optional<StudyBuddyConnection> connection =
                            studyBuddyConnectionRepository.findByFollowerAndFollowing(currentUser, u);
                    String relationshipStatus = connection.isPresent() ? connection.get().getStatus().name() : "NONE";
                    return toDTO(u, relationshipStatus);
                })
                .collect(Collectors.toList());
    }

    public BuddyProfileDTO getProfile(Long userId) {
        User currentUser = getCurrentUser();
        Optional<User> targetOpt = userRepository.findById(userId);
        if (!targetOpt.isPresent()) return null;
        User target = targetOpt.get();

        Optional<StudyBuddyConnection> connection =
                studyBuddyConnectionRepository.findByFollowerAndFollowing(currentUser, target);
        String relationshipStatus = connection.isPresent() ? connection.get().getStatus().name() : "NONE";

        boolean visible = target.isPublic()
                || target.getId().equals(currentUser.getId())
                || "ACCEPTED".equals(relationshipStatus);

        BuddyProfileDTO dto = new BuddyProfileDTO();
        dto.id = target.getId();
        dto.username = target.getUsername();
        dto.name = target.getName();
        dto.isPublic = target.isPublic();
        dto.relationshipStatus = relationshipStatus;
        dto.visible = visible;

        if (visible) {
            dto.streakCount = target.getStreakCount();
            dto.xpTotal = target.getXpTotal();
            dto.badges = badgeService.getBadges(target);
        }

        return dto;
    }

    private StudyBuddyDTO toDTO(User user, String relationshipStatus) {
        return new StudyBuddyDTO(user.getId(), user.getUsername(), user.getName(), relationshipStatus);
    }
}
