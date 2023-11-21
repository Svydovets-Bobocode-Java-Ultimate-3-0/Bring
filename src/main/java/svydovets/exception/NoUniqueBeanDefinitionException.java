package svydovets.exception;

public class NoUniqueBeanDefinitionException extends RuntimeException {

    public NoUniqueBeanDefinitionException(String message) {
        super(message);
    }

    public NoUniqueBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
