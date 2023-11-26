package svydovets.util;

public abstract class ErrorMessages {
    public static final String NO_BEAN_DEFINITION_FOUND_OF_TYPE = "No bean definition found of type %s";
    public static final String NO_BEAN_FOUND_OF_TYPE = "No bean found of type %s";
    public static final String NO_UNIQUE_BEAN_FOUND_OF_TYPE = "No unique bean found of type %s";
    public static final String NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE = "No unique bean definition found of type %s";
    public static final String NO_MATCHING_PATTERN_FOUND_EXCEPTION = "No matching pattern found for the request path [%s]";
    public static final String NO_UNIQUE_PATTERN_FOUND_EXCEPTION = "Check your patch on valid: [%s]";

    public static final String NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH = "Request path [%s] parsing error from pattern path [%s]";

    public static final String UNSUPPORTED_TYPE_ERROR_MESSAGE = "Unsupported parameter type: %s";
    public static final String UNSUPPORTED_ANNOTATION_ERROR_MESSAGE = "Unsupported annotation: %s";
    public static final String UNSUPPORTED_NUMBER_TYPE_ERROR_MESSAGE = "Unsupported number type: %s";
}
