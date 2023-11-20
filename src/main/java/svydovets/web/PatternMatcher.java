package svydovets.web;

import java.util.HashMap;
import java.util.Map;

public class PatternMatcher {

  private static final String SPLITERATOR = "/";

  public static Map<String, Map<String, String>> findMatchingPatterns(
      String[] patternPaths, String requestPath) {
    Map<String, Map<String, String>> matchingResults = new HashMap<>();

    String requestPaths = requestPath.split("\\?")[0];

    for (String patternPath : patternPaths) {
      try {
        if (patternPath.equals(requestPaths)) {
          matchingResults.put(patternPath, new HashMap<>());
        } else {
          Map<String, String> variableValues = getVariables(patternPath, requestPaths);
          matchingResults.put(patternPath, variableValues);
        }
      } catch (IllegalArgumentException e) {
        // Patterns weren't match. Try the next pattern
      }
    }

//    if (matchingResults.size() > 1) {
//      // todo: We can add logs
//      throw new RuntimeException("Check your path");
//    }

    if (matchingResults.isEmpty()) {
      throw new RuntimeException("No matching pattern found for the request path");
    }

    return matchingResults;
  }

  public static Map<String, String> getVariables(String patternPath, String requestPath) {
    String[] patternLines = patternPath.split(SPLITERATOR);
    String[] requestLines = requestPath.split(SPLITERATOR);

    if (patternLines.length != requestLines.length) {
      throw new IllegalArgumentException("Paths have different length. Check your patch on valid.");
    }

    Map<String, String> variableValues = new HashMap<>();

    for (int i = 0; i < patternLines.length; i++) {
      if (patternLines[i].matches("\\{\\w+\\}")) {
        String variableName = patternLines[i].substring(1, patternLines[i].length() - 1);
        variableValues.put(variableName, requestLines[i]);
      } else if (!patternLines[i].equals(requestLines[i])) {
        throw new IllegalArgumentException("Paths do not match");
      }
    }

    return variableValues;
  }

  public static void main(String[] args) {
    String[] patternPaths = {
      "/users/{id}/list",
      "/users/note/list",
      "/users/{id}/notes/body",
      "/users/{id}",
      "/users/{id}/card"
    };

    String requestPath = "/users/note/list";

    Map<String, Map<String, String>> matchingResults = // Map { key -> pattern path, value -> Map{ key -> variableName, value -> value from request } }
        findMatchingPatterns(patternPaths, requestPath);

    System.out.println(matchingResults);
  }
}
