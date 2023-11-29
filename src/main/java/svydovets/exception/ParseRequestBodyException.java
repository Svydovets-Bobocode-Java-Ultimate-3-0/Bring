package svydovets.exception;

public class ParseRequestBodyException extends RuntimeException {

    public ParseRequestBodyException(String message) {
        super(message);
    }

    public ParseRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }
}
