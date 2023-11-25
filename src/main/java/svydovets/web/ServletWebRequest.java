package svydovets.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.web.path.RequestPathParser;
import svydovets.web.path.RequestPathParserImpl;

import java.util.Map;
import java.util.stream.Collectors;

import static svydovets.web.DispatcherServlet.CONTROLLER_REDIRECT_REQUEST_PATH;

public class ServletWebRequest {
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
        if (pathVariableValuesMap == null) {
            RequestPathParser requestPathParser = new RequestPathParserImpl();
            pathVariableValuesMap = requestPathParser.parse(request.getPathInfo(), (String) request.getAttribute(CONTROLLER_REDIRECT_REQUEST_PATH));
        }
        return pathVariableValuesMap.get(parameterName);
    }

    public String getRequestParameterValue(String parameterName) {
        String[] requestParameters = request.getParameterMap().get(parameterName);

        return requestParameters == null
                ? null
                : requestParameters[0];
    }

    public Object getRequestBody(Class<?> parameterType) {
        if (requestBody == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            requestBody = parseRequestBody(objectMapper, parameterType);
        }
        return this.requestBody;
    }

    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    private Object parseRequestBody(ObjectMapper objectMapper, Class<?> parameterType) {
        try {
            String jsonRequestBody = request.getReader()
                    .lines()
                    .collect(Collectors.joining());
            return objectMapper.readValue(jsonRequestBody, parameterType);
        } catch (Exception e) {
            // todo: LOG ERROR
            throw new RuntimeException("Error processing JSON request body", e);
        }
    }
}
