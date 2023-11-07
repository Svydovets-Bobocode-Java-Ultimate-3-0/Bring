package svydovets.exception;

public class NoDefaultConstructor extends RuntimeException {
    public NoDefaultConstructor(String message) {
        super(message);
    }

    public NoDefaultConstructor(String message, Throwable cause) {
        super(message, cause);
    }

}
