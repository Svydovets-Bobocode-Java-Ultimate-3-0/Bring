package svydovets.web.exception;

/**
 * Exception thrown when no matching pattern is found for a given request path
 * within a set of predefined pattern paths.
 */
public class NoMatchingPatternFoundException extends RuntimeException {

    public NoMatchingPatternFoundException(String message) {
        super(message);
    }
}
