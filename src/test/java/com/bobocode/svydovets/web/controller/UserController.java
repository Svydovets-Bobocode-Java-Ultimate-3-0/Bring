package com.bobocode.svydovets.web.controller;

import com.bobocode.svydovets.web.dto.User;
import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RestController;
import svydovets.web.dto.MediaType;
import svydovets.web.dto.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<User> getOne(@PathVariable Long id) {
        var user = new User(23, "firstName");
       return ResponseEntity.ok()
               .contentType(MediaType.APPLICATION_JSON)
               .contentLength(130)
               .header("Content-Type", "application/json")
               .body(user);
    }
}