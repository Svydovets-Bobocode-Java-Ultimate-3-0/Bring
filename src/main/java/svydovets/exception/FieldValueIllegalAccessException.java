package svydovets.exception;

public class FieldValueIllegalAccessException extends RuntimeException {

    public FieldValueIllegalAccessException(String message) {
        super(message);
    }

    public FieldValueIllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
