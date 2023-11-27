package svydovets.exception;

public class UnsupportedScopeException extends RuntimeException {

    public UnsupportedScopeException(String message) {
        super(message);
    }

    public UnsupportedScopeException(String message, Throwable cause) {
        super(message, cause);
    }
}
