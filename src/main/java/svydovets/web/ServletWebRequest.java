package svydovets.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.exception.ParseRequestBodyException;
import svydovets.web.path.RequestPathParser;
import svydovets.web.path.RequestPathParserImpl;

import java.util.Map;
import java.util.stream.Collectors;

import static svydovets.util.ErrorMessageConstants.ERROR_PROCESSING_JSON_REQUEST_BODY;
import static svydovets.web.DispatcherServlet.CONTROLLER_REDIRECT_REQUEST_PATH;

public class ServletWebRequest {

    private static final Logger log = LoggerFactory.getLogger(ServletWebRequest.class);

    static final ObjectMapper objectMapper = new ObjectMapper();

    private static final RequestPathParser requestPathParser = new RequestPathParserImpl();

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    private Map<String, String> pathVariableValuesMap;
    private Object requestBody;

    public ServletWebRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getPathVariableValue(String parameterName) {
        log.trace("Call getPathVariableValue({})", parameterName);
        if (pathVariableValuesMap == null) {
            pathVariableValuesMap = requestPathParser
                    .parse(request.getServletPath(), (String) request.getAttribute(CONTROLLER_REDIRECT_REQUEST_PATH));
        }

        return pathVariableValuesMap.get(parameterName);
    }

    public String getRequestParameterValue(String parameterName) {
        return request.getParameter(parameterName);
    }

    public Object getRequestBody(Class<?> parameterType) {
        if (requestBody == null) {
            requestBody = parseRequestBody(parameterType);
        }
        return this.requestBody;
    }

    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    private Object parseRequestBody(Class<?> parameterType) {
        try {
            String jsonRequestBody = request.getReader()
                    .lines()
                    .collect(Collectors.joining());
            return objectMapper.readValue(jsonRequestBody, parameterType);
        } catch (Exception exception) {
            log.trace(ERROR_PROCESSING_JSON_REQUEST_BODY);
            log.trace(exception.getMessage());

            throw new ParseRequestBodyException(ERROR_PROCESSING_JSON_REQUEST_BODY, exception);
        }
    }
}
