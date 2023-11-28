package svydovets.web.path;

import java.util.Map;

public record RequestInfo(Map<String, String> pathVariableValuesMap, Map<String, String[]> requestParameterValuesMap, String requestBody) {
}
