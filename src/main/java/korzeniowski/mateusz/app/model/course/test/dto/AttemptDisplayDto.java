package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Attempt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class AttemptDisplayDto {
    private LocalDateTime endedAt;
    private String status;
    private Double score;
    private Double mark;

    public AttemptDisplayDto(LocalDateTime endedAt, String status, Double score, Double mark) {
        this.endedAt = endedAt;
        this.status = status;
        this.score = score;
        this.mark = mark;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMark() {
        return mark;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public String getFormattedEndedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
        return endedAt.format(formatter);
    }

    public static AttemptDisplayDto map(Attempt attempt) {
        return new AttemptDisplayDto(
                attempt.getEndedAt(),
                attempt.getStatus().name(),
                attempt.getScore(),
                attempt.getMark());
    }
}
