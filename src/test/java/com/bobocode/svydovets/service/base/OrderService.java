package com.bobocode.svydovets.service.base;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class OrderService {

    private MessageService messageService;
    public CommonService commonService;

    public MessageService getMessageService() {
        return messageService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    //should not hold @Autowired for test purpose
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    public CommonService getCommonService() {
        return commonService;
    }
}
