package korzeniowski.mateusz.app.exceptions;

public class GroupNameAlreadyExistsException extends RuntimeException {
    public GroupNameAlreadyExistsException(String message) {
        super(message);
    }
}
