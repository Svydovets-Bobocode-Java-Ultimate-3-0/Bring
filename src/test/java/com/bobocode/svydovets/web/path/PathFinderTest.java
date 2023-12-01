package com.bobocode.svydovets.web.path;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.web.exception.NoMatchingPatternFoundException;
import svydovets.web.exception.NoUniquePatternFoundException;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;
import svydovets.web.path.RequestPathParser;
import svydovets.web.path.RequestPathParserImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PathFinderTest {

    private static final String REQUEST_PATH_1 = "/users/1/notes/5";

    private static final String REQUEST_PATH_2 = "/users/def/notes/noteDef";

    private PathFinder pathFinder;

    private RequestPathParser requestPathParser;

    @BeforeEach
    void setUp() {
        this.pathFinder = new PathFinderImpl();
        this.requestPathParser = new RequestPathParserImpl();
    }

    @Test
    public void shouldThrowNoMatchingPatternFoundExceptionWhenNotFoundRequestPath() {
        Set<String> controllerPathMap = getDefaultControllerPaths();

        var exception = assertThrows(NoMatchingPatternFoundException.class,
                () -> pathFinder.find(REQUEST_PATH_1, controllerPathMap));
        assertEquals(String.format("No matching pattern found for the request path [%s]", REQUEST_PATH_1), exception.getMessage());
    }

    @Test
    public void shouldThrowNoUniquePatternFoundExceptionWhenFoundSeveralRequestPath() {
        Set<String> controllerPathMap = new HashSet<>();
        controllerPathMap.add("/users/{id}");
        controllerPathMap.add("/users/{type}");
        controllerPathMap.add("/users/type");
        controllerPathMap.add("/users/id");

        List<String> list = new ArrayList<>();
        list.add("/users/{type}");
        list.add("/users/{id}");

        var exception = assertThrows(NoUniquePatternFoundException.class,
                () -> pathFinder.find("/users/1", controllerPathMap));
        assertEquals(String.format("Check your patch on valid: [%s]", list), exception.getMessage());

        exception = assertThrows(NoUniquePatternFoundException.class,
                () -> pathFinder.find("/users/open", controllerPathMap));
        assertEquals(String.format("Check your patch on valid: [%s]", list), exception.getMessage());
    }

    @Test
    public void shouldMatchingPatternFoundForRequestPath() {
        Set<String> controllerPathMap = getDefaultControllerPaths();
        controllerPathMap.add("/users/{id}/notes/{noteId}");

        String result = pathFinder.find(REQUEST_PATH_2, controllerPathMap);
        assertEquals(REQUEST_PATH_2, result);

        result = pathFinder.find(REQUEST_PATH_1, controllerPathMap);
        String[] patternLines = getLinesBySpliterator(REQUEST_PATH_1);
        String[] resultLines = getLinesBySpliterator(result);

        assertEquals(patternLines.length, resultLines.length);
        Map<String, String> map = requestPathParser.parse(REQUEST_PATH_1, result);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("id", "1");
        expectedMap.put("noteId", "5");
        assertTrue(checkMapAndExpectedMap(map, expectedMap));
    }

    private String[] getLinesBySpliterator(String path) {
        return path.split("/");
    }

    @NotNull
    private static Set<String> getDefaultControllerPaths() {
        Set<String> controllerPathMap = new HashSet<>();

        controllerPathMap.add("/users/{id}/card");
        controllerPathMap.add("/users/{id}/list");
        controllerPathMap.add("/users/note/list");
        controllerPathMap.add("/users/{id}/notes/body");
        controllerPathMap.add("/users/{id}");
        controllerPathMap.add("/users/def/notes/noteDef");

        return controllerPathMap;
    }

    private boolean checkMapAndExpectedMap(Map<String, String> map, Map<String, String> expectedMap) {
        if (map.size() != expectedMap.size()) {
            return false;
        }

        return map.entrySet().stream()
                .allMatch(entry -> {
                    var key = entry.getKey();
                    if (!expectedMap.containsKey(key)) {
                        return false;
                    }

                    var expectedValue = expectedMap.get(key);

                    return expectedValue.equals(entry.getValue());
                });
    }
}
