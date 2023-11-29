package svydovets.exception;

public class UnresolvedCircularDependencyException extends RuntimeException {

    public UnresolvedCircularDependencyException(String message) {
        super(message);
    }

    public UnresolvedCircularDependencyException(String message, Throwable cause) {
        super(message, cause);
    }
}
