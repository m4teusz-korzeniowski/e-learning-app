package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AttemptState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String answersGivenJson;
    @OneToOne
    private Attempt attempt;
    @Enumerated(EnumType.STRING)
    private AttemptStatus status;
    private Integer currentQuestionAttempt;
    private LocalDateTime lastModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnswersGivenJson() {
        return answersGivenJson;
    }

    public void setAnswersGivenJson(String answersGivenJson) {
        this.answersGivenJson = answersGivenJson;
    }

    public Attempt getAttempt() {
        return attempt;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public Integer getCurrentQuestionAttempt() {
        return currentQuestionAttempt;
    }

    public void setCurrentQuestionAttempt(Integer currentQuestionAttempt) {
        this.currentQuestionAttempt = currentQuestionAttempt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
