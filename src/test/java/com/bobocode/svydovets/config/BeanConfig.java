package com.bobocode.svydovets.config;

import com.bobocode.svydovets.service.TrimService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

/**
 * Config class for scanning a specified package and creating a context with additional beans
 */
@Configuration
@ComponentScan("com.bobocode.svydovets.service")
public class BeanConfig {
    @Bean
    public TrimService printService() {
        return new TrimService();
    }
}
