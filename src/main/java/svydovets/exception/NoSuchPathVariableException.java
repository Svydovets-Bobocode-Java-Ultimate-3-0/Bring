package svydovets.exception;

public class NoSuchPathVariableException extends RuntimeException {

    public NoSuchPathVariableException(String message) {
        super(message);
    }

    public NoSuchPathVariableException(String message, Throwable cause) {
        super(message, cause);
    }
}
