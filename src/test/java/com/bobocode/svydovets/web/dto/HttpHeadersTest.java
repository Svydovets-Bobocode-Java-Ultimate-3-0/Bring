package com.bobocode.svydovets.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.web.dto.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpHeadersTest {

    private HttpHeaders httpHeaders;

    @BeforeEach
    void init() {
        this.httpHeaders = new HttpHeaders();
    }

    @Test
    public void shouldReturnSizeHeaders() {
        httpHeaders.setHeader("head", "test");
        assertEquals(1, httpHeaders.getHeaders().size());
    }

    @Test
    public void shouldReturnHeaderByKey() {
        httpHeaders.setHeader("head", "test");
        assertTrue(httpHeaders.getHeaders().containsKey("head"));
        assertEquals("test", httpHeaders.getHeaders().get("head"));
    }
}
