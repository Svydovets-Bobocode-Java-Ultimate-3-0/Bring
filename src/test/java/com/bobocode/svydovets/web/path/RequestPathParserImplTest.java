package com.bobocode.svydovets.web.path;

import org.junit.jupiter.api.*;
import svydovets.web.exception.NoSuchPathVariableException;
import svydovets.web.path.RequestPathParser;
import svydovets.web.path.RequestPathParserImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RequestPathParserImplTest {

    private static final String REQUEST_PATH_1 = "/users/1/notes/open";

    private static final String PATTERN_PATH_1 = "/users/{id}/notes/{type}";

    private static final String PATTERN_PATH_2 = "/users/full/notes/type";

    private static final String PATTERN_PATH_3 = "/users/full/{id}/notes/{type}";

    private RequestPathParser requestPathParser;

    @BeforeEach
    void setUp() {
        this.requestPathParser = new RequestPathParserImpl();
    }

    @Test
    @Order(1)
    public void shouldThrowNoSuchPathVariableExceptionWhenParseRequestPath() {
        String parsErrMsg = "Request path [%s] parsing error from pattern path [%s]";
        var exception = assertThrows(NoSuchPathVariableException.class,
                () -> requestPathParser.parse(REQUEST_PATH_1, PATTERN_PATH_2));
        assertEquals(String.format(parsErrMsg, REQUEST_PATH_1, PATTERN_PATH_2), exception.getMessage());

        exception = assertThrows(NoSuchPathVariableException.class,
                () -> requestPathParser.parse(REQUEST_PATH_1, PATTERN_PATH_3));
        assertEquals(String.format(parsErrMsg, REQUEST_PATH_1, PATTERN_PATH_3), exception.getMessage());
    }

    @Test
    @Order(2)
    public void shouldCreatePathVariableMapFromRequestPath() {
        Map<String, String> resultMap = requestPathParser.parse(REQUEST_PATH_1, PATTERN_PATH_1);
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("id", "1");
        expectedMap.put("type", "open");

        assertEquals(expectedMap.size(), resultMap.size());
        assertTrue(checkAllEntryMap(resultMap, expectedMap));
    }

    private boolean checkAllEntryMap(Map<String, String> resultMap, Map<String, String> expectedMap) {
        return resultMap.entrySet().stream()
                .allMatch(entry -> expectedMap.get(entry.getKey()).equals(entry.getValue()));
    }

}