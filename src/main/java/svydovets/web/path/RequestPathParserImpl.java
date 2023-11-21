package svydovets.web.path;

import svydovets.exception.NoSuchPathVariableException;

import java.util.HashMap;
import java.util.Map;

import static svydovets.web.path.PathFinder.ALPHANUMERIC_REGEX;
import static svydovets.web.path.PathFinder.SPLITERATOR;

public class RequestPathParserImpl implements RequestPathParser {

    private static final String NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH = "Request path [%s] parsing error from pattern path [%s]";

    @Override
    public Map<String, String> parse(String requestPath, String patternPath) {
        String[] requestLines = requestPath.split(SPLITERATOR);
        String[] patternLines = patternPath.split(SPLITERATOR);

        if (requestLines.length != patternLines.length) {
            String parsingErrMsg = String.format(NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH, requestPath, patternPath);
            throw new NoSuchPathVariableException(parsingErrMsg);
        }

        Map<String, String> pathVariables = new HashMap<>();
        for (int i = 1; i < patternLines.length; i++) {
            if (patternLines[i].matches(ALPHANUMERIC_REGEX)) {
                pathVariables.put(getVariableKey(patternLines[i]), requestLines[i]);
            }
        }

        if (pathVariables.isEmpty()) {
            String parsingErrMsg = String.format(NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH, requestPath, patternPath);
            throw new NoSuchPathVariableException(parsingErrMsg);
        }

        return pathVariables;
    }

    private String getVariableKey(String patternLine) {
        return patternLine.substring(1, patternLine.length() - 1);
    }
}
