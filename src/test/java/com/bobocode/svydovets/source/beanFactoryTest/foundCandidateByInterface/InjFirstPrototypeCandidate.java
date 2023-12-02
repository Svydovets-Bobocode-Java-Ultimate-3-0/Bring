package com.bobocode.svydovets.source.beanFactoryTest.foundCandidateByInterface;

import svydovets.core.annotation.Component;
import svydovets.core.annotation.Primary;
import svydovets.core.annotation.Scope;
import svydovets.core.context.ApplicationContext;

@Component
@Primary
@Scope(ApplicationContext.SCOPE_PROTOTYPE)
public class InjFirstPrototypeCandidate implements InjPrototypeCandidate {
}
