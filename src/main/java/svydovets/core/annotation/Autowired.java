package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     The Autowired annotation automatically injects beans into your code, freeing you from having to wire them together manually.
 * </p>
 * <p>
 *     When annotating constructor, field, or method with @Autowired it should inject bean of the required type into that constructor, field, or method.
 * </p>
 * <p>
 *     It will look for bean that matches the required type and automatically inject it.
 *     This is form of dependency injection, design principle that helps to make code cleaner and more modular.
 * </p>
 * <p>
 *     Autowired annotation works with fields, methods, and constructors:
 * </p>
 *
 * <p>
 *     In this example, constructor of MyBean is annotated with @Autowired.
 *     This means that when creates instance of MyBean,
 *     it will automatically create instance of DependencyBean, and pass it to the constructor.
 * </p>
 * <pre class="code">
 * public class MyBean {
 * private DependencyBean dependency;
 *     &#064;Autowired
 *     public MyBean(DependencyBean dependency) {
 *         this.dependency = dependency;
 *     }
 * }
 * </pre>
 *
 * <p>
 *     In this case, will automatically inject instance of DependencyBean
 *     into dependency field after it has created the MyBean instance.
 * </p>
 * <pre class="code">
 * public class MyBean {
 *     &#064;Autowired
 *     private DependencyBean dependency;
 * }
 * </pre>
 *
 * <p>
 *     In this case, when creates instance of MyBean, will automatically create instance of DependencyBean,
 *     and pass it to setDependency method.
 * </p>
 * <pre class="code">
 * public class MyBean {
 *     private DependencyBean dependency;
 *     &#064;Autowired
 *     public void setDependency(DependencyBean dependency) {
 *         this.dependency = dependency;
 *     }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface Autowired {
}
