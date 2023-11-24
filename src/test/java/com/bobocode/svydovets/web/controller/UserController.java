package com.bobocode.svydovets.web.controller;

import com.bobocode.svydovets.web.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PathVariable;
import svydovets.web.annotation.PostMapping;
import svydovets.web.annotation.PutMapping;
import svydovets.web.annotation.RequestBody;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RequestParam;
import svydovets.web.annotation.RestController;

import java.lang.annotation.Annotation;

import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_FIRST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_ID;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_LAST_NAME;
import static com.bobocode.svydovets.web.factory.UserFactory.DEFAULT_STATUS;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public User getOneById(@PathVariable Long id) {
        return new User(id, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_STATUS);
    }

    @GetMapping
    public User getOneByFirstName(@RequestParam String firstName, HttpServletRequest req, HttpServletResponse resp) {
        return new User(DEFAULT_ID, firstName, DEFAULT_LAST_NAME, DEFAULT_STATUS);
    }

    @PostMapping
    public User save(@RequestBody User user) {
        return user;
    }

    @PutMapping
    public User update(@PathVariable Long id, @RequestParam String status, @RequestBody User user) {
        return new User(++id, user.getFirstName(), user.getLastName(), status);
    }
}
