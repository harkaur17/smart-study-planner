package studyPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "join_requests", uniqueConstraints = @UniqueConstraint(columnNames = { "requester_id", "session_id" }))
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession session;

    public enum Status {
        PENDING, ACCEPTED
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public JoinRequest() {
    }

    public JoinRequest(User requester, StudySession session) {
        this.requester = requester;
        this.session = session;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getRequester() {
        return requester;
    }

    public StudySession getSession() {
        return session;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public void setSession(StudySession session) {
        this.session = session;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
