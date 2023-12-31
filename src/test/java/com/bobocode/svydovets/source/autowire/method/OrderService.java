package com.bobocode.svydovets.source.autowire.method;

import com.bobocode.svydovets.source.base.CommonService;
import com.bobocode.svydovets.source.base.MessageService;
import com.bobocode.svydovets.source.base.NullService;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;

@Component
public class OrderService {

    private MessageService messageService;
    private CommonService commonService;
    private NullService nullService;


    @Autowired
    public void setMessageService(MessageService messageService, CommonService commonService) {
        this.messageService = messageService;
        this.commonService = commonService;
    }

    //should not have @Autowired for test purpose
    public void setNullService(NullService nullService) {
        this.nullService = nullService;
    }


    public NullService getNullService() {
        return nullService;
    }


    public CommonService getCommonService() {
        return commonService;
    }

    public MessageService getMessageService() {
        return messageService;
    }


}
