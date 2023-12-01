package com.bobocode.svydovets.ioc.util;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;
import svydovets.util.exception.NoDefaultConstructorException;
import svydovets.util.ErrorMessageConstants;
import svydovets.util.ReflectionsUtil;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReflectionUtilTest {

    @Test
    @Order(1)
    public void findAutowiredFieldNamesTest() {
        List<String> fieldNames = ReflectionsUtil.findAutowiredFieldNames(PersonService.class);
        assertEquals(2, fieldNames.size());
        assertEquals("editService", fieldNames.get(0));
        assertEquals("commonService", fieldNames.get(1));
    }

    @Test
    @Order(2)
    public void notFoundAutowiredFieldNamesTest() {
        List<String> fieldNames = ReflectionsUtil.findAutowiredFieldNames(EditService.class);
        assertEquals(0, fieldNames.size());
    }

    @Test
    @Order(3)
    public void findPreparedNoArgsConstructorTest() throws NoSuchMethodException {
        Constructor<EditService> constructor = ReflectionsUtil.getPreparedNoArgsConstructor(EditService.class);
        var targetType = EditService.class;
        assertEquals(targetType.getDeclaredConstructor(), constructor);
        assertEquals(0, constructor.getParameterTypes().length);
    }

    @Test
    @Order(4)
    public void notFoundPreparedNoArgsConstructorWithNoDefaultConstructorThrowTest() {
        var exception = assertThrows(NoDefaultConstructorException.class,
                () -> ReflectionsUtil.getPreparedNoArgsConstructor(UserService.class));
        assertEquals(String.format(ErrorMessageConstants.NO_DEFAULT_CONSTRUCTOR_FOUND_OF_TYPE, UserService.class.getName()), exception.getMessage());
    }

    @Component
    static class PersonService {

        @Autowired
        private EditService editService;

        @Autowired
        private CommonService commonService;

        private MessageService messageService;
    }

    @Component
    static class EditService {

    }

    @Component
    static class UserService {

        private final MessageService messageService;

        public UserService(MessageService messageService) {
            this.messageService = messageService;
        }
    }

}
