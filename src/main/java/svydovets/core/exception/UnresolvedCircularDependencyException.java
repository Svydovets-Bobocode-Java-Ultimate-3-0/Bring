package svydovets.core.exception;

public class UnresolvedCircularDependencyException extends RuntimeException {

    public UnresolvedCircularDependencyException(String message) {
        super(message);
    }
}
