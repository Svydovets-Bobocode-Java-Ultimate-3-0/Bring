package svydovets.core.exception;

public class AutowireBeanException extends RuntimeException{
    public AutowireBeanException(String message) {
        super(message);
    }

    public AutowireBeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
