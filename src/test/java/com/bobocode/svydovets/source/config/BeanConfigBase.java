package com.bobocode.svydovets.source.config;

import com.bobocode.svydovets.source.autowire.method.TrimService;
import com.bobocode.svydovets.source.base.MessageService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

/**
 * Config class for scanning a specified package and creating a context with additional beans
 */
@Configuration
@ComponentScan("com.bobocode.svydovets.source.base")
public class BeanConfigBase {
    @Bean("trimServiceCustomName")
    public TrimService trimService() {
        return new TrimService();
    }
    @Bean
    public MessageService messageService() {
        return new MessageService();
    }
}
