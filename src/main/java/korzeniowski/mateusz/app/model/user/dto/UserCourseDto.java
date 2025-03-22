package korzeniowski.mateusz.app.model.user.dto;

import korzeniowski.mateusz.app.model.course.Course;


public class UserCourseDto {
    private final Long id;

    public UserCourseDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static UserCourseDto map(Course course) {
        return new UserCourseDto(course.getId());
    }
}
