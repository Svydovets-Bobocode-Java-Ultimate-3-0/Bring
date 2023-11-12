package svydovets.exception;

public class InvalidInvokePostConstructMethodException extends RuntimeException  {

    public InvalidInvokePostConstructMethodException(String message) {
        super(message);
    }

    public InvalidInvokePostConstructMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
