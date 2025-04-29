package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.user.User;

import java.time.LocalDateTime;

@Entity
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    @Enumerated(EnumType.STRING)
    private AttemptStatus status;
    private Double score;
    private Double mark;
    @Lob
    private String answersGivenJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getAnswersGivenJson() {
        return answersGivenJson;
    }

    public void setAnswersGivenJson(String answersGivenJson) {
        this.answersGivenJson = answersGivenJson;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }
}
