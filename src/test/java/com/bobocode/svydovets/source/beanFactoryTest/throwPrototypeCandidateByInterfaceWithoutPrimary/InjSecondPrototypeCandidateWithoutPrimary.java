package com.bobocode.svydovets.source.beanFactoryTest.throwPrototypeCandidateByInterfaceWithoutPrimary;

import svydovets.core.annotation.Component;
import svydovets.core.annotation.Scope;
import svydovets.core.context.ApplicationContext;

@Component
@Scope(ApplicationContext.SCOPE_PROTOTYPE)
public class InjSecondPrototypeCandidateWithoutPrimary implements InjPrototypeCandidateWithoutPrimary {

}
