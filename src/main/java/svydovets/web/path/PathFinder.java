package svydovets.web.path;

import java.util.List;
import java.util.Set;

public interface PathFinder {

    String ALPHANUMERIC_REGEX = "\\{\\w+\\}";

    String SPLITERATOR = "/";

    /**
     *
     * @param requestPath
     * @param patternPaths
     * @return
     */
    String find(String requestPath, Set<String> patternPaths);

}
