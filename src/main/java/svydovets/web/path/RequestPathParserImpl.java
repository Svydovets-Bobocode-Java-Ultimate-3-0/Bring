package svydovets.web.path;

import svydovets.exception.NoSuchPathVariableException;
import svydovets.util.ErrorMessages;

import java.util.HashMap;
import java.util.Map;

import static svydovets.web.path.PathFinder.ALPHANUMERIC_REGEX;
import static svydovets.web.path.PathFinder.SPLITERATOR;

/**
 * Implementation of the {@link RequestPathParser} interface that parses a given request path
 * based on a specified pattern path, extracting variables and their values.
 */
public class RequestPathParserImpl implements RequestPathParser {


    /**
     * Parses the given request path based on the specified pattern path and extracts variables.
     *
     * @param requestPath The request path to be parsed.
     * @param patternPath The pattern path used as a template for parsing.
     * @return A map of variable names to their corresponding values extracted from the request path.
     * @throws NoSuchPathVariableException if the parsing fails due to mismatched path lengths or no variables found.
     */
    @Override
    public Map<String, String> parse(String requestPath, String patternPath) {
        String[] requestLines = requestPath.split(PathFinder.REQ_PARAM_SPLITERATOR)[0].split(SPLITERATOR);
        String[] patternLines = patternPath.split(SPLITERATOR);

        if (requestLines.length != patternLines.length) {
            String parsingErrMsg = String.format(ErrorMessages.NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH, requestPath, patternPath);
            throw new NoSuchPathVariableException(parsingErrMsg);
        }

        Map<String, String> pathVariables = new HashMap<>();
        for (int i = 1; i < patternLines.length; i++) {
            if (patternLines[i].matches(ALPHANUMERIC_REGEX)) {
                pathVariables.put(getVariableKey(patternLines[i]), requestLines[i]);
            }
        }

        if (pathVariables.isEmpty()) {
            String parsingErrMsg = String.format(ErrorMessages.NO_SUCH_PATH_VARIABLES_FOR_THIS_REQUEST_PATH, requestPath, patternPath);
            throw new NoSuchPathVariableException(parsingErrMsg);
        }

        return pathVariables;
    }

    private String getVariableKey(String patternLine) {
        return patternLine.substring(1, patternLine.length() - 1);
    }
}
