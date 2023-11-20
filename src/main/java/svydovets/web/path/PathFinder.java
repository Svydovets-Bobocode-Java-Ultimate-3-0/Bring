package svydovets.web.path;

import java.util.List;
import java.util.Set;

public interface PathFinder {

    /**
     *
     * @param requestPath
     * @param patternPaths
     * @return
     */
    String find(String requestPath, Set<String> patternPaths);

}
