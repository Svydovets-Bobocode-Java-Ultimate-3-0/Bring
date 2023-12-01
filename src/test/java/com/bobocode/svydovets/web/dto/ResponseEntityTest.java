package com.bobocode.svydovets.web.dto;

import org.junit.jupiter.api.*;
import svydovets.web.dto.HttpHeaders;
import svydovets.web.dto.HttpStatus;
import svydovets.web.dto.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ResponseEntityTest {

    private ResponseEntity<String> responseEntity;

    @BeforeEach
    void init() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setHeader("head", "test");
        this.responseEntity = new ResponseEntity<>("body", httpHeaders, HttpStatus.OK);
    }

    @Test
    @Order(1)
    public void shouldReturnBodyField() {
        assertEquals("body", responseEntity.getBody());
    }

    @Test
    @Order(2)
    public void shouldReturnHeaderField() {
        var headers = responseEntity.getHttpHeaders();
        assertEquals(1, headers.getHeaders().size());
        assertEquals("test", headers.getHeaders().get("head"));
    }

    @Test
    @Order(3)
    public void shouldReturnStatusField() {
        assertEquals(HttpStatus.OK, responseEntity.getHttpStatus());
    }
}
