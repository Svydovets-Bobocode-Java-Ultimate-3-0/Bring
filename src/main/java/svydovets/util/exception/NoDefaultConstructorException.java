package svydovets.util.exception;

public class NoDefaultConstructorException extends RuntimeException {

    public NoDefaultConstructorException(String message, Throwable cause) {
        super(message, cause);
    }

}
