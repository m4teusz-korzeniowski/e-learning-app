package korzeniowski.mateusz.app.model.course.dto;

import korzeniowski.mateusz.app.model.course.Course;

public class CourseNameDto {
    private final String name;
    private final Long creatorId;
    private String creatorName;

    public CourseNameDto(String name, Long creatorId) {
        this.name = name;
        this.creatorId = creatorId;
    }

    public String getName() {
        return name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public static CourseNameDto map (Course course) {
        return new CourseNameDto(course.getName(), course.getCreatorId());
    }
}
