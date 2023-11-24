package com.bobocode.svydovets.web.controller;

import com.bobocode.svydovets.web.dto.User;
import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RestController;
import svydovets.web.dto.ResponseEntity;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public ResponseEntity<User> getOne(@PathVariable Long id) {
        var user = new User(23, "firstName");
       //return ResponseEntity.ok().body(user);
       return ResponseEntity.ok()
               .header("Content-Type", "application/json")
               .body(user);

    }
}