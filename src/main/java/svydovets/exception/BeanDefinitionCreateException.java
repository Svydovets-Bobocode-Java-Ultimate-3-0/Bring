package svydovets.exception;

public class BeanDefinitionCreateException extends RuntimeException {

    public BeanDefinitionCreateException(String message) {
        super(message);
    }

    public BeanDefinitionCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
