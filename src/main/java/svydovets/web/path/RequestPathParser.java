package svydovets.web.path;

import java.util.Map;

public interface RequestPathParser {

  Map<String, String> parse(String requestPath, String patternPath);

}
