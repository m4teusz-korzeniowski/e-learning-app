package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TestDisplayDto {
    private Long id;
    private String name;
    private String description;
    private String moduleName;
    private String courseName;
    private Long courseId;
    private Integer maxAttempts;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TestDisplayDto(Long id, String name, String description, String moduleName, String courseName,
                          Long courseId, Integer maxAttempts, Integer duration,
                          LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.moduleName = moduleName;
        this.courseName = courseName;
        this.courseId = courseId;
        this.maxAttempts = maxAttempts;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getFormattedStartTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
        return startTime.format(formatter);
    }

    public String getFormattedEndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "d MMMM yyyy, HH:mm", new Locale("pl", "PL"));
        return endTime.format(formatter);
    }

    public static TestDisplayDto map(Test test) {
        return new TestDisplayDto(
                test.getId(),
                test.getName(),
                test.getDescription(),
                test.getModule().getName(),
                test.getModule().getCourse().getName(),
                test.getModule().getCourse().getId(),
                test.getMaxAttempts(),
                test.getDuration(),
                test.getStartTime(),
                test.getEndTime()
        );
    }
}
