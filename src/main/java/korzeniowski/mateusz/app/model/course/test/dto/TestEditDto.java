package korzeniowski.mateusz.app.model.course.test.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import korzeniowski.mateusz.app.model.course.test.Test;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class TestEditDto {
    private Long id;
    private String name;
    private String description;
    @NotNull(message = "*pole nie może być puste!")
    @Min(value = 1, message = "*test powinien mieć co najmniej jedno pytanie!")
    private Integer numberOfQuestions;
    @Min(value = 10, message = "*minimalny czas trwania testu to 10 minut!")
    private Integer duration;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime start;
    @Future(message = "*wymagana jest data z przyszłości!")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime end;
    @Min(value = 1, message = "*jeżeli pole nie jest puste, wymagane jest co najmniej jedno podejście!")
    @Max(value = 10, message = "*maksymalna ilość podejść wynosi 10!")
    private Integer maxAttempts;
    private Long courseId;
    private Boolean overviewEnabled;

    public TestEditDto(Long id, String name, String description,
                       Integer numberOfQuestions, Integer duration,
                       LocalDateTime start, LocalDateTime end, Integer maxAttempts,
                       Long courseId, Boolean overviewEnabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.numberOfQuestions = numberOfQuestions;
        this.duration = duration;
        this.start = start;
        this.end = end;
        this.maxAttempts = maxAttempts;
        this.courseId = courseId;
        this.overviewEnabled = overviewEnabled;
    }

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Boolean getOverviewEnabled() {
        return overviewEnabled;
    }

    public void setOverviewEnabled(Boolean overviewEnabled) {
        this.overviewEnabled = overviewEnabled;
    }

    public static TestEditDto map(Test test) {
        return new TestEditDto(
                test.getId(),
                test.getName(),
                test.getDescription(),
                test.getNumberOfQuestions(),
                test.getDuration(),
                test.getStartTime(),
                test.getEndTime(),
                test.getMaxAttempts(),
                test.getModule().getCourse().getId(),
                test.getOverviewEnabled()
        );
    }
}
