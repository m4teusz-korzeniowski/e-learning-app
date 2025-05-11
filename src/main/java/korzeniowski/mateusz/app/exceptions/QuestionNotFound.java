package korzeniowski.mateusz.app.exceptions;

public class QuestionNotFound extends RuntimeException {
    public QuestionNotFound(String message) {
        super(message);
    }
}
