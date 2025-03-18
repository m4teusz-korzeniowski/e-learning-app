package korzeniowski.mateusz.app.model.course.dto;

import java.util.List;

public class TeacherCourseEnrollDto {
    private String userEmail;
    private String courseName;


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
