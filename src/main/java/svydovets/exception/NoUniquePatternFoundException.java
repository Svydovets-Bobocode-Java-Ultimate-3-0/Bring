package svydovets.exception;

public class NoUniquePatternFoundException extends RuntimeException {

    public NoUniquePatternFoundException(String message) {
        super(message);
    }

    public NoUniquePatternFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
