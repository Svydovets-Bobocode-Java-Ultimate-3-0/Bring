package com.bobocode.svydovets.web.factory;

import com.bobocode.svydovets.web.dto.User;

public class UserFactory {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_FIRST_NAME = "TestFirstName";
    public static final String DEFAULT_LAST_NAME = "TestLastName";
    public static final String DEFAULT_STATUS = "NEW";

    public static User createDefaultUser() {
        return new User(DEFAULT_ID, DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_STATUS);
    }
}
