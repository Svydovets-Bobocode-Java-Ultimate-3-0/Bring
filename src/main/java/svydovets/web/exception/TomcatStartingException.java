package svydovets.web.exception;

public class TomcatStartingException extends RuntimeException {

    public TomcatStartingException(String message) {
        super(message);
    }

    public TomcatStartingException(String message, Throwable cause) {
        super(message, cause);
    }
}
