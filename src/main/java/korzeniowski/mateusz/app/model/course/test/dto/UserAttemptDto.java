package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Attempt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserAttemptDto {
    private Long attemptId;
    private Double mark;
    private LocalDateTime endTime;

    public UserAttemptDto(Long attemptId, Double mark, LocalDateTime endTime) {
        this.attemptId = attemptId;
        this.mark = mark;
        this.endTime = endTime;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public String getFormattedEndedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
        return endTime.format(formatter);
    }


    public static UserAttemptDto map(Attempt attempt) {
        return new UserAttemptDto(attempt.getId(), attempt.getMark(), attempt.getEndedAt());
    }
}
