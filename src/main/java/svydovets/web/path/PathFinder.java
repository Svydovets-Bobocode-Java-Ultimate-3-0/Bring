package svydovets.web.path;

import java.util.Set;

/**
 * Identifies the pattern path for a given request path from a set of predefined pattern paths.
 */
public interface PathFinder {

    /**
     * Regular expression representing an alphanumeric pattern to find variable name.
     */
    String ALPHANUMERIC_REGEX = "\\{\\w+\\}";

    /**
     * The default separator used to split path segments.
     */
    String SPLITERATOR = "/";

    String REQ_PARAM_SPLITERATOR = "\\?";


    /**
     * Finds the pattern path for the given request path from the set of pattern paths.
     *
     * @param requestPath   The request path for which to find the matching pattern.
     * @param patternPaths  The set of predefined pattern paths.
     * @return              The pattern path.
     */
    String find(String requestPath, Set<String> patternPaths);

}
