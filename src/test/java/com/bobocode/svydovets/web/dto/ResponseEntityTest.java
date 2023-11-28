package com.bobocode.svydovets.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
    public void shouldReturnBodyField() {
        assertEquals("body", responseEntity.getBody());
    }

    @Test
    public void shouldReturnHeaderField() {
        var headers = responseEntity.getHeaders();
        assertEquals(1, headers.getHeaders().size());
        assertEquals("test", headers.getHeaders().get("head"));
    }

    @Test
    public void shouldReturnStatusField() {
        assertEquals(HttpStatus.OK, responseEntity.getStatus());
    }
}
