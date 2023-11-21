package svydovets.web.path;

import java.util.Map;

public interface RequestPathParser {

  //todo: implement this method
  // requestPath = "/users/1/notes/5"
  // patternPath = "/users/{id}/notes/{type}"
  // return Map<String, String> {
  //  id : 1,
  //  type : 5
  //  }
  Map<String, String> parse(String requestPath, String patternPath);

}
