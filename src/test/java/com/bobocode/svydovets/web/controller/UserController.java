package com.bobocode.svydovets.web.controller;

import com.bobocode.svydovets.web.dto.User;
import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public User getOne(@PathVariable Long id) {
        return new User(id, "TestFirstName", "TestLastName");
    }
}
