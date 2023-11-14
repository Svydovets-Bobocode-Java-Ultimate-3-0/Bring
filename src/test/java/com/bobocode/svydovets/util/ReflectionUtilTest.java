package com.bobocode.svydovets.util;

import com.bobocode.svydovets.service.base.CommonService;
import com.bobocode.svydovets.service.base.EditService;
import com.bobocode.svydovets.service.base.MessageService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import svydovets.core.annotation.Autowired;
import svydovets.core.annotation.Component;
import svydovets.core.annotation.Qualifier;
import svydovets.util.ReflectionsUtil;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReflectionUtilTest {

    @Test
    public void findAutowiredFieldNamesTest() {
        List<String> fieldNames = ReflectionsUtil.findAutowiredFieldNames(PersonService.class);
        assertThat(fieldNames.size()).isEqualTo(2);
        assertThat(fieldNames.get(0)).isEqualTo("editService");
        assertThat(fieldNames.get(1)).isEqualTo("commonService");
    }

    @Test
    public void findAutowiredFieldNamesThatWereMarkedQualifier() {
        List<String> fieldNames = ReflectionsUtil.findAutowiredFieldNames(PersonQualifierService.class);

        assertThat(fieldNames.size()).isEqualTo(2);

        assertThat(fieldNames.get(0)).isEqualTo("customEditService");
        assertThat(fieldNames.get(1)).isEqualTo("qualifierCommonService");
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
    static class PersonQualifierService {

        @Autowired
        @Qualifier("customEditService")
        private EditService editService;

        @Autowired
        @Qualifier("qualifierCommonService")
        private CommonService commonService;

    }
}
