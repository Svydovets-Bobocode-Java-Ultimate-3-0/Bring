package svydovets.util;

public abstract class ErrorMessageConstants {

    public static final String NO_BEAN_DEFINITION_FOUND_OF_TYPE = "No bean definition found of type %s";

    public static final String NO_BEAN_FOUND_OF_TYPE = "No bean found of type %s";

    public static final String NO_UNIQUE_BEAN_FOUND_OF_TYPE = "No unique bean found of type %s";

    public static final String NO_UNIQUE_BEAN_DEFINITION_FOUND_OF_TYPE = "No unique bean definition found of type %s";

    public static final String ERROR_CREATING_BEAN_DEFINITION_FOR_BEAN_WITH_INVALID_CONSTRUCTORS = "Error creating bean definition for bean '%s': Invalid autowire-marked constructor: %s. Found constructor with 'required' Autowired annotation already: %s";
    public static final String CIRCULAR_DEPENDENCY_DETECTED = "Circular dependency has been detected for %s";

    public static final String NO_MATCHING_PATTERN_FOUND_EXCEPTION = "No matching pattern found for the request path [%s]";

    public static final String NO_UNIQUE_PATTERN_FOUND_EXCEPTION = "Check your patch on valid: [%s]";

    public static final String NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH = "Request path [%s] parsing error from pattern path [%s]";

    public static final String UNSUPPORTED_SCOPE_TYPE = "Unsupported scope type: %s";

    public static final String UNSUPPORTED_TYPE_ERROR_MESSAGE = "Unsupported parameter type: %s";

    public static final String UNSUPPORTED_ANNOTATION_ERROR_MESSAGE = "Unsupported annotation: %s";

    public static final String UNSUPPORTED_NUMBER_TYPE_ERROR_MESSAGE = "Unsupported number type: %s";

    public static final String ERROR_PROCESSING_JSON_REQUEST_BODY = "Error processing JSON request body";

    public static final String ERROR_READING_JSON_REQUEST_BODY = "Error reading JSON request body";

    public static final String NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE = "No default constructor found of type %s";

    public static final String ERROR_AUTOWIRED_BEAN_EXCEPTION_MESSAGE = "There is access to %s field";

    public static final String ERROR_NOT_SET_ACCESSIBLE_FOR_FIELD = "Not set accessible for field: %s";

    public static final String ERROR_NOT_SUPPORT_DEPENDENCY_INJECT_TO_COLLECTION = "We don't support dependency injection into collection of type: %s";

    public static final String ERROR_NO_ACCESS_TO_METHOD = "There is no access to method";

    public static final String REQUEST_PROCESSING_ERROR = "Error processing request: %s %s";

    public static final String ERROR_CREATED_BEAN_OF_TYPE = "Error creating bean of type '%s'";

    public static final String ERROR_THE_METHOD_THAT_WAS_ANNOTATED_WITH_POST_CONSTRUCT = "Something went wrong. Please check the method that was annotated with @PostConstruct";

    public static final String ERROR_NOT_UNIQUE_METHOD_THAT_ANNOTATED_POST_CONSTRUCT = "You cannot have more than one method that is annotated with @PostConstruct.";

}
