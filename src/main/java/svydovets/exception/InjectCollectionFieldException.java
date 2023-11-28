package svydovets.exception;

public class InjectCollectionFieldException extends RuntimeException {

    public InjectCollectionFieldException(String message) {
        super(message);
    }

    public InjectCollectionFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
