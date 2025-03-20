package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Test;

public class TestDisplayDto {
    private Long id;
    private String name;
    private String description;
    private String moduleName;
    private String courseName;
    private Long courseId;

    public TestDisplayDto(Long id, String name, String description, String moduleName, String courseName, Long courseId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.moduleName = moduleName;
        this.courseName = courseName;
        this.courseId = courseId;
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

    public static TestDisplayDto map(Test test) {
        return new TestDisplayDto(
                test.getId(),
                test.getName(),
                test.getDescription(),
                test.getModule().getName(),
                test.getModule().getCourse().getName(),
                test.getModule().getCourse().getId()
        );
    }
}
