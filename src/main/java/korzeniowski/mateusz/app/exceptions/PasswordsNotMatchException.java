package korzeniowski.mateusz.app.exceptions;

public class PasswordsNotMatchException extends RuntimeException {
    public PasswordsNotMatchException(String message) {
        super(message);
    }
}
