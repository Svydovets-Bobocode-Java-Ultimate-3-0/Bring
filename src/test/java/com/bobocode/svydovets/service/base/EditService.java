package com.bobocode.svydovets.service.base;

import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

/**
 * Class for testing "autowiring" logic via field
 */
@Component
public class EditService {
    @Autowired
    private MessageService messageService;

    private CommonService commonService; //service shouldn't be injected
    public String editMessage() {
        return String.format("***%s***", messageService.getMessage());
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public CommonService getCommonService() {
        return commonService;
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }
}
