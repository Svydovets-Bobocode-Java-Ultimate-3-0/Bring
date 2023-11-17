package com.bobocode.svydovets.source.config;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.base.NullService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.ComponentScan;
import svydovets.core.annotation.Configuration;

@Configuration
@ComponentScan("com.bobocode.svydovets.source.autowire")
public class AutowirePackageBeansConfig {

    @Bean
    public MessageService messageService() {
        return new MessageService();
    }

    @Bean
    public CommonService commonService() {
        return new CommonService();
    }

    @Bean
    public NullService nullService() {
        return new NullService();
    }
}
