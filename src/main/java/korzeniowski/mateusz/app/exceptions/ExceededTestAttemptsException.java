package korzeniowski.mateusz.app.exceptions;

public class ExceededTestAttemptsException extends RuntimeException {
    public ExceededTestAttemptsException(String message) {
        super(message);
    }
}
