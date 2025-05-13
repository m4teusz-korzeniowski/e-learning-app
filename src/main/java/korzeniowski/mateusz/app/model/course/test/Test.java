package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.course.module.Module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer numberOfQuestions;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxAttempts;
    private Boolean overviewEnabled;
    @OneToMany(mappedBy = "test")
    private List<Attempt> attempts;
    @OneToMany(mappedBy = "test")
    private List<Question> questions = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name="module_id")
    private Module module;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime start) {
        this.startTime = start;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime end) {
        this.endTime = end;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<Attempt> attempts) {
        this.attempts = attempts;
    }

    public Boolean getOverviewEnabled() {
        return overviewEnabled;
    }

    public void setOverviewEnabled(Boolean overviewEnabled) {
        this.overviewEnabled = overviewEnabled;
    }
}
