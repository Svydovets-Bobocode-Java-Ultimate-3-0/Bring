package com.bobocode.svydovets.source.autowire.method;

import com.bobocode.svydovets.source.base.CommonService;
import svydovets.core.annotation.Autowired;

/**
 * Class for testing "autowiring" logic via setter. Creates as bean in the {@link BeanConfig} class
 */
public class TrimService {
    private CommonService commonService;

    public CommonService getCommonService() {
        return commonService;
    }

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }
}
