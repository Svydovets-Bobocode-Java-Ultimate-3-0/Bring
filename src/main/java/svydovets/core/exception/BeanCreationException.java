package svydovets.core.exception;

public class BeanCreationException extends RuntimeException {
    public BeanCreationException(String message) {
        super(message);
    }
    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
