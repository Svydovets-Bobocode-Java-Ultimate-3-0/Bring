package svydovets.exception;

public class ExtractRequestBodyException extends RuntimeException {

    public ExtractRequestBodyException(String message) {
        super(message);
    }

    public ExtractRequestBodyException(String message, Throwable cause) {
        super(message, cause);
    }

}
