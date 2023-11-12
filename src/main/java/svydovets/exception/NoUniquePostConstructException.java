package svydovets.exception;

public class NoUniquePostConstructException extends RuntimeException {

  public NoUniquePostConstructException(String message) {
    super(message);
  }

  public NoUniquePostConstructException(String message, Throwable cause) {
    super(message, cause);
  }
}
