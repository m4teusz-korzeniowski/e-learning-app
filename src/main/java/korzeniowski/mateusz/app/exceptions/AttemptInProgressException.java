package korzeniowski.mateusz.app.exceptions;

public class AttemptInProgressException extends RuntimeException {
    public AttemptInProgressException(String message) {
        super(message);
    }
}
