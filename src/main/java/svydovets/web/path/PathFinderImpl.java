package svydovets.web.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.exception.NoMatchingPatternFoundException;
import svydovets.exception.NoUniquePatternFoundException;
import svydovets.util.ErrorMessageConstants;

import java.util.List;
import java.util.Set;

public class PathFinderImpl implements PathFinder {

    private static final Logger log = LoggerFactory.getLogger(PathFinderImpl.class);

    /**
     * Finds the pattern path for the given request path from the set of pattern paths.
     *
     * @param requestPath  The request path for which to find the matching pattern.
     * @param patternPaths The set of predefined pattern paths.
     * @return The pattern path.
     * @throws NoMatchingPatternFoundException if no matching pattern is found for the given request path.
     * @throws NoUniquePatternFoundException   if multiple matching patterns are found for the given request path.
     */
    @Override
    public String find(String requestPath, Set<String> patternPaths) {
        log.trace("Call find({}, {})", requestPath, patternPaths);
        if (patternPaths.contains(requestPath)) {
            log.trace("Found patternPath equals with requestPath: {}", requestPath);

            return requestPath;
        }

        String fullRequestPath = requestPath.split(REQ_PARAM_SPLITERATOR)[0];

        List<String> patternPathList = findAll(fullRequestPath, patternPaths);

        if (patternPathList == null || patternPathList.isEmpty()) {
            throw new NoMatchingPatternFoundException(String.format(ErrorMessageConstants.NO_MATCHING_PATTERN_FOUND_EXCEPTION, requestPath));
        } else if (patternPathList.size() > 1) {
            throw new NoUniquePatternFoundException(String.format(ErrorMessageConstants.NO_UNIQUE_PATTERN_FOUND_EXCEPTION, patternPathList));
        }

        String patternPath = patternPathList.get(0);

        log.trace("Found patternPath: {} for requestPath: {}", patternPath, requestPath);

        return patternPath;
    }

    private List<String> findAll(String requestPath, Set<String> patternPaths) {
        log.trace("Call findAll({}, {})", requestPath, patternPaths);
        String[] requestLines = getLinesBySpliterator(requestPath);
        return patternPaths.stream()
                .filter(patternPath -> getLinesBySpliterator(patternPath).length == requestLines.length)
                .filter(patternPath -> filterPatternPath(requestLines, getLinesBySpliterator(patternPath)))
                .toList();
    }

    private boolean filterPatternPath(String[] requestLines, String[] patternPath) {
        boolean foundPath;
        for (int i = 1; i < requestLines.length; i++) {
            foundPath = requestLines[i].equals(patternPath[i]) || patternPath[i].matches(ALPHANUMERIC_REGEX);

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
