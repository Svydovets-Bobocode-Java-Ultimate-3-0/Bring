package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The @Configuration annotation is used to indicate that a class declares one or more @Bean methods, and define user's custom beans. The @Configuration annotation is an integral part of the creating application context.
 *
 * <h2>Purpose</h2>
 * <ul>
 *     <li>The primary purpose of the @Configuration annotation is to define Java-based configuration classes</li>
 *     <li>It is used to mark a class as a configuration class, indicating that it contains bean definitions.</li>
 * </ul>
 *
 * <h2>How It Works</h2>
 * <p>
 *     During the application startup, the Spring IoC container scans the classpath for classes annotated with {@code @Configuration}.
 *     For each {@code @Configuration} class found, the container processes {@code @Bean} methods within that class.
 *     {@code @Bean} methods are invoked, and the objects they return are registered as Spring beans.
 *     Dependencies between beans are resolved, and the Spring IoC container manages the lifecycle of these beans.
 * </p>
 *
 * <h2>Bean Declaration</h2>
 * <ul>
 *     <li>Within a class annotated with @Configuration, you can declare bean definitions using methods annotated with @Bean</li>
 *     <li>These @Bean methods return instances of the objects that should be managed by the Bring IoC container.</li>
 * </ul>
 * <pre class="code">
 * &#064;Configuration
 * public class TestConfig {
 *     &#064;Bean
 *     public MessageService messageService() {
 *         MessageService messageService = new MessageService();
 *         messageService.setMessage("Hello from \"MessageService\"");
 *         return messageService;
 *     }
 * }
 * </pre>
 *
 * <h2>Context Configuration</h2>
 * <ul>
 *     <li>Annotated classes are typically used in conjunction with the AnnotationConfigApplicationContext or
 *     AnnotationConfigWebApplicationContext to load the context based on Java configuration. For examole:</li>
 * </ul>
 *
 * <pre class="code">
 * AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
 * </pre>
 *
 * <h2>Component Scanning</h2>
 * <ul>
 *     <li>In addition to manually declaring beans using @Bean methods, @Configuration classes often leverage component scanning.
 *     Component scanning automatically detects and registers components (beans) in specified packages. For example:</li>
 * </ul>
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan(com.example.beans)
 * public class TestConfig {
 *     &#064;Bean
 *     public MessageService messageService() {
 *         MessageService messageService = new MessageService();
 *         messageService.setMessage("Hello from \"MessageService\"");
 *         return messageService;
 *     }
 * }
 * </pre>
 *
 * <h2>Bean Dependencies</h2>
 * <ul>
 *     <li>@Configuration classes can express dependencies between beans, ensuring that beans are created in the
 *     correct order and with the required dependencies. For example:</li>
 * </ul>
 * <pre class="code">
 * &#064;Configuration
 * public class TestConfig {
 *     &#064;Bean
 *     public MessageService messageService() {
 *         MessageService messageService = new MessageService();
 *         messageService.setMessage("Hello from \"MessageService\"");
 *         return messageService;
 *     }
 *     &#064;Bean
 *     public PrintService printService(MessageService messageService) {
 *         return new PrintService(messageService);
 *     }
 * }
 * </pre>
 *
 * @see Bean
 * @see ComponentScan
 * @see svydovets.core.context.ApplicationContext
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Configuration {
    String value() default "";
}
