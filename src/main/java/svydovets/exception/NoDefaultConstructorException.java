package svydovets.exception;

public class NoDefaultConstructorException extends RuntimeException {

    public NoDefaultConstructorException(String message) {
        super(message);
    }

    public NoDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

}
