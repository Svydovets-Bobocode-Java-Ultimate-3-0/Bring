package svydovets.core.annotation;

import svydovets.core.context.ApplicationContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD}) //todo: ???
public @interface Scope {
    String value() default ApplicationContext.SCOPE_SINGLETON;
}
