package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.user.User;

import java.time.LocalDateTime;

@Entity
public class Attempt {
    @EmbeddedId
    private AttemptId id;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @MapsId("testId")
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public AttemptId getId() {
        return id;
    }

    public void setId(AttemptId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
