package korzeniowski.mateusz.app.model.course.dto;

import korzeniowski.mateusz.app.model.course.Course;

public class TeacherCourseDto {
    private String name;

    public TeacherCourseDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static TeacherCourseDto map(Course course) {
        return new TeacherCourseDto(course.getName());
    }
}
