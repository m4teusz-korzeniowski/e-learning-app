package korzeniowski.mateusz.app.exceptions;

public class UserAlreadyEnrolledForCourse extends RuntimeException {
    public UserAlreadyEnrolledForCourse(String message) {
        super(message);
    }
}
