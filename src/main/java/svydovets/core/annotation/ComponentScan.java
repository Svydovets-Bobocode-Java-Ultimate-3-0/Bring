package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for enabling component scanning.
 * <p>
 * The {@code @ComponentScan} annotation is used to specify the base packages to scan for components (e.g., beans,
 * controllers) within an application context. It facilitates automatic discovery and registration of
 * annotated classes, allowing to avoid explicit configuration of individual beans.
 * Example usage:
 * <pre>
 * {@code
 *   @ComponentScan("com.example.beans")
 *   public class TestConfig {
 *       // Class with component scanning enabled
 *   }
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * @Configuration
 * @ComponentScan("com.example.beans")
 * public class TestConfig {
 *     // Configuration class with component scanning enabled
 *  }
 * }
 * </pre>
 * In this example, the {@code TestConfig} class is annotated with {@code @ComponentScan}, specifying the base package
 * "com.example.beans" for component scanning. The Bring will scan this package and its subpackages for classes annotated
 * with {@code @Component}, {@code @Configuration}, {@code @RestController}, and related annotations.
 * <p>
 * The {@code @ComponentScan} annotation supports a {@link #value()} attribute, allowing developers to specify the
 * base package or packages to scan. If not specified, the default value is an empty string, and the Bring will not scan
 * the package in this case
 * <p>
 *
 * @see Configuration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentScan {
    /**
     * The base package to scan for components.
     * <p>
     * If not specified, the default value is an empty string
     *
     * @return the base package or packages to scan for components
     */
    String value() default "";
}

