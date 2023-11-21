package com.bobocode.svydovets.ioc.web.path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.exception.NoMatchingPatternFoundException;
import svydovets.exception.NoUniquePatternFoundException;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PathFinderTest {

    private static final String REQUEST_PATH_1 = "/users/1/notes/5";

    private static final String REQUEST_PATH_2 = "/users/def/notes/noteDef";

    private PathFinder pathFinder;

    @BeforeEach
    void setUp() {
        this.pathFinder = new PathFinderImpl();
    }

    @Test
    public void shouldThrowNoMatchingPatternFoundExceptionWhenNotFoundRequestPath() {
        Set<String> controllerPathMap = new HashSet<>();
        controllerPathMap.add("/users/{id}/card");
        controllerPathMap.add("/users/{id}/list");
        controllerPathMap.add("/users/note/list");
        controllerPathMap.add("/users/{id}/notes/body");
        controllerPathMap.add("/users/{id}");
        controllerPathMap.add("/users/def/notes/noteDef");

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
        Set<String> controllerPathMap = new HashSet<>();
        controllerPathMap.add("/users/{id}/card");
        controllerPathMap.add("/users/{id}/list");
        controllerPathMap.add("/users/note/list");
        controllerPathMap.add("/users/{id}/notes/body");
        controllerPathMap.add("/users/{id}");
        controllerPathMap.add("/users/{id}/notes/{noteId}");
        controllerPathMap.add("/users/def/notes/noteDef");

        String result = pathFinder.find(REQUEST_PATH_2, controllerPathMap);
        assertEquals(REQUEST_PATH_2, result);

        result = pathFinder.find(REQUEST_PATH_1, controllerPathMap);
        String[] patternLines = getLinesBySpliterator(REQUEST_PATH_1);
        String[] resultLines = getLinesBySpliterator(result);

        //todo: after impl RequestPathParser add 1 test check map
//        assertEquals(REQUEST_PATH_1, result);
        assertEquals(patternLines.length, resultLines.length);
    }

    private String[] getLinesBySpliterator(String path) {
        return path.split("/");
    }
}
