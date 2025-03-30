package korzeniowski.mateusz.app.model.course.dto;


import java.util.List;

public class TeacherCourseEnrollDto {
    private List<String> emails;
    private String courseName;
    private Long courseId;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
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
}
