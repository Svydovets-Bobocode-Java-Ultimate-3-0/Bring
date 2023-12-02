package svydovets.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.web.exception.ParseRequestBodyException;
import svydovets.web.path.RequestPathParser;
import svydovets.web.path.RequestPathParserImpl;

import java.util.Map;
import java.util.stream.Collectors;

import static svydovets.util.ErrorMessageConstants.ERROR_PROCESSING_JSON_REQUEST_BODY;
import static svydovets.web.DispatcherServlet.CONTROLLER_REDIRECT_REQUEST_PATH;

/**
 * The {@code ServletWebRequest} class represents a web request in a Servlet environment, providing
 * utility methods to interact with the HTTP request and response.
 *
 * <p>This class is designed to encapsulate and provide utility methods for handling HTTP requests and responses
 * in a Servlet environment. It acts as a wrapper around the standard HttpServletRequest and HttpServletResponse objects,
 * offering convenient methods to access and manipulate various components of the web request.</p>
 *
 * <p>It includes functionality for handling path variables, request parameters, request body,
 * request attributes, and more.
 *
 * <p>Internally, it utilizes an {@link ObjectMapper} for JSON processing and a {@link RequestPathParser}
 * for parsing path variables.
 *
 * <p>Instances of this class are typically used within a {@link DispatcherServlet} to facilitate
 * handling and processing of incoming HTTP requests.
 *
 * @see HttpServletRequest
 * @see HttpServletResponse
 */
public class ServletWebRequest {

    private static final Logger log = LoggerFactory.getLogger(ServletWebRequest.class);

    static final ObjectMapper objectMapper = new ObjectMapper();

    private static final RequestPathParser requestPathParser = new RequestPathParserImpl();

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private Map<String, String> pathVariableValuesMap;
    private Object requestBody;

    /**
     * Constructs a {@code ServletWebRequest} instance with the given {@link HttpServletRequest}
     * and {@link HttpServletResponse}.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     */
    public ServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Gets the underlying {@link HttpServletRequest}.
     *
     * @return the HTTP request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Gets the underlying {@link HttpServletResponse}.
     *
     * @return the HTTP response
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Method retrieves the value of a specified path variable from the request. It internally uses a {@link RequestPathParser}
     * to parse and store path variables for subsequent access.
     *
     * @param parameterName the name of the path variable
     * @return the value of the path variable
     */
    public String getPathVariableValue(String parameterName) {
        log.trace("Call getPathVariableValue({})", parameterName);
        if (pathVariableValuesMap == null) {
            pathVariableValuesMap = requestPathParser
                    .parse(request.getServletPath(), (String) request.getAttribute(CONTROLLER_REDIRECT_REQUEST_PATH));
        }

        return pathVariableValuesMap.get(parameterName);
    }

    /**
     * Retrieves the value of the specified request parameter.
     *
     * @param parameterName the name of the request parameter
     * @return the value of the request parameter
     */
    public String getRequestParameterValue(String parameterName) {
        return request.getParameter(parameterName);
    }

    /**
     * Retrieves and parses the request body into the specified type. It uses the Jackson ObjectMapper to deserialize the JSON request body into the specified class type
     *
     * @param parameterType the class type to parse the request body into
     * @return the parsed request body
     * @throws ParseRequestBodyException if an error occurs during parsing
     */
    public Object getRequestBody(Class<?> parameterType) {
        if (requestBody == null) {
            requestBody = parseRequestBody(parameterType);
        }
        return this.requestBody;
    }

    /**
     * Sets an attribute in the underlying {@link HttpServletRequest}.
     *
     * @param name  the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    /**
     * Gets the value of the specified attribute from the underlying {@link HttpServletRequest}.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    /**
     * Parses the request body into the specified type using the configured {@link ObjectMapper}.
     *
     * @param parameterType the class type to parse the request body into
     * @return the parsed request body
     * @throws ParseRequestBodyException if an error occurs during parsing
     */
    private Object parseRequestBody(Class<?> parameterType) {
        try {
            String jsonRequestBody = request.getReader()
                    .lines()
                    .collect(Collectors.joining());
            return objectMapper.readValue(jsonRequestBody, parameterType);
        } catch (Exception exception) {
            log.error(exception.getMessage());

            throw new ParseRequestBodyException(ERROR_PROCESSING_JSON_REQUEST_BODY, exception);
        }
    }
}

