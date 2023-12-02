package svydovets.web.path;

import java.util.Map;

/**
 * Extracts variables from a request path based on a specified pattern path.
 */
public interface RequestPathParser {

  /**
   * Parses the given request path based on the specified pattern path and extracts variables.
   *
   * @param requestPath   The request path to be parsed.
   * @param patternPath   The pattern path used as a template for parsing.
   * @return              A map of variable names to their corresponding values extracted from the request path.
   */
  Map<String, String> parse(String requestPath, String patternPath);

}
