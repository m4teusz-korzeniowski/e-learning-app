package korzeniowski.mateusz.app.model.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import korzeniowski.mateusz.app.model.course.Course;

public class CourseCreationDto {
    @NotBlank
    @NotNull
    private String name;
    private String description;
    private Long creatorId;

    public CourseCreationDto(String name, String description, Long creatorId) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public static CourseCreationDto map(Course course) {
        return new CourseCreationDto(course.getName(), course.getDescription(), course.getCreatorId());
    }
}
