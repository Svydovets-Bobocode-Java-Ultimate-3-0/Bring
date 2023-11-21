package svydovets.exception;

public class NoMatchingPatternFoundException extends RuntimeException {

    public NoMatchingPatternFoundException(String message) {
        super(message);
    }

    public NoMatchingPatternFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
