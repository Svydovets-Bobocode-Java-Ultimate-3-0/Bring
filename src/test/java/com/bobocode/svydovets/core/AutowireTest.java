package com.bobocode.svydovets.core;

import com.bobocode.svydovets.service.base.EditService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import svydovets.core.context.ApplicationContext;
import svydovets.core.context.DefaultApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
public class AutowireTest {

    @Test
    void shouldNotNullWhenFieldIsInjected(){
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getMessageService()).isNotNull();
    }

    @Test
    void shouldReturnNullIfFieldNotInjected(){
        ApplicationContext context = new DefaultApplicationContext("com.bobocode.svydovets.service.base");
        EditService editService = context.getBean(EditService.class);
        assertThat(editService.getCommonService()).isNull();
    }
}
