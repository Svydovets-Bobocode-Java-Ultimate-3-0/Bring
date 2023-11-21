package svydovets.web.path;

import svydovets.exception.NoMatchingPatternFoundException;
import svydovets.exception.NoUniquePatternFoundException;
import svydovets.web.path.PathFinder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathFinderImpl implements PathFinder {

    private static final String SPLITERATOR = "/";

    private static final String REQ_PARAM_SPLITERATOR = "\\?";

    private static final String ALPHANUMERIC_REGEX = "\\{\\w+\\}";

    private static final String NO_MATCHING_PATTERN_FOUND_EXCEPTION = "No matching pattern found for the request path [%s]";

    private static final String NO_UNIQUE_PATTERN_FOUND_EXCEPTION = "Check your patch on valid: [%s]";

    @Override
    public String find(String requestPath, Set<String> patternPaths) {
        if (patternPaths.contains(requestPath)) {
            return requestPath;
        }

        String fullRequestPath = requestPath.split(REQ_PARAM_SPLITERATOR)[0];

        List<String> patternPathList = findAll(fullRequestPath, patternPaths);

        if (patternPathList == null || patternPathList.isEmpty()) {
            throw new NoMatchingPatternFoundException(String.format(NO_MATCHING_PATTERN_FOUND_EXCEPTION, requestPath));
        } else if (patternPathList.size() > 1) {
            throw new NoUniquePatternFoundException(String.format(NO_UNIQUE_PATTERN_FOUND_EXCEPTION, patternPathList));
        }

        return patternPathList.get(0);
    }

    private List<String> findAll(String requestPath, Set<String> patternPaths) {
        String[] requestLines = getLinesBySpliterator(requestPath);
        return patternPaths.stream()
                .filter(patternPath -> getLinesBySpliterator(patternPath).length == requestLines.length)
                .filter(patternPath -> filterPatternPath(requestLines, getLinesBySpliterator(patternPath)))
                .collect(Collectors.toList());
    }

    private boolean filterPatternPath(String[] requestLines, String[] patternPath) {
        boolean foundPath = true;
        for (int i = 1; i < requestLines.length; i++) {
            boolean found = requestLines[i].equals(patternPath[i]) || patternPath[i].matches(ALPHANUMERIC_REGEX);
            foundPath = foundPath && found;

            if (!foundPath) {
                return false;
            }
        }

        return true;
    }

    private String[] getLinesBySpliterator(String path) {
        return path.split(SPLITERATOR);
    }
}
