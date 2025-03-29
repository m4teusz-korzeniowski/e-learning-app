package korzeniowski.mateusz.app.exceptions;

public class PeselAlreadyInUseException extends RuntimeException {

    public PeselAlreadyInUseException(String message) {
        super(message);
    }
}
