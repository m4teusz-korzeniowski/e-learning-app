package korzeniowski.mateusz.app.model.course.dto;

import korzeniowski.mateusz.app.model.course.Course;

public class TeacherCourseDto {
    private String name;
    private Long id;

    public TeacherCourseDto(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static TeacherCourseDto map(Course course) {
        return new TeacherCourseDto(course.getName(), course.getId());
    }
}
