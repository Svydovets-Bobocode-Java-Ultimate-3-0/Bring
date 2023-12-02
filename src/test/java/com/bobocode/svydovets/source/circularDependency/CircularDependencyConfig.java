package com.bobocode.svydovets.source.circularDependency;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import svydovets.core.annotation.Bean;
import svydovets.core.annotation.Configuration;

@Configuration
public class CircularDependencyConfig {

    @Bean
    public CommonService commonService(MessageService messageService) {
        return new CommonService();
    }

    @Bean
    public MessageService messageService(CommonService commonService) {
        return new MessageService();
    }
}
