package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Configuration annotation is used to indicate that a class declares one or more @Bean methods, and its purpose
 * is to define user's custom beans. . The @Configuration annotation is an integral part of the
 * Java-based configuration approach in Spring, providing an alternative to XML-based configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Configuration {
    String value() default "";
}
