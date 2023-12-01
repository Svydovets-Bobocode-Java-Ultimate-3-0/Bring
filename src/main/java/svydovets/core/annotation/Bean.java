package svydovets.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Bean is object that is instantiated, assembled, and managed by the IoC container
 *     The Bean annotation is a custom annotation that you can use in your own implementation of the Bring framework.
 *     It is used to mark methods in code that produce bean that should be managed.
 * </p>
 *
 * <p>
 *     When annotate method with @Bean, are telling the ApplicationContext that return value of that method should be registered as bean.
 *     The ApplicationContext will call that method and register the return value as bean.
 *     This way, ApplicationContext can manage the lifecycle of that object, just like any other bean.
 * </p>
 *
 * <p>
 *     The value attribute of bean annotation is optional.
 *     If specified, it will be used as the component name.
 *     If not provided, the name of the method will be used as bean name.
 * </p>
 *
 * <p>Here are examples of how to use the Bean annotation:</p>
 *
 * <p>
 *     In this example, the createMyBean method is annotated with @Bean.
 *     This means that when ApplicationContext starts up, it will call createMyBean and register the returned MyBean object as bean named "myBean".
 *     <pre class="code">
 *         &#064;Bean("myBean")
 *         public MyBean createMyBean() {
 *             return new MyBean();
 *         }
 *     </pre>
 * </p>
 *
 * <p>
 *     <p>In this example, the createMyBean method has a parameter of type DependencyBean.
 *     When the ApplicationContext calls createMyBean, it will look for a bean of type DependencyBean,
 *            and pass it as an argument. This way, MyBean instance gets its dependency automatically.</p>
 *
 *     <p>If method annotated with @Bean has parameters, the parameters themselves are treated
 *                                                    as dependencies that need to be injected.
 *     ApplicationContext will try to find bean of the required type and inject it as parameter when
 *                                                                               it calls the method.
 *     This is form of automatic dependency injection.
 *     If ApplicationContext cannot find bean of the required type to inject, it will throw BeanCreationException.
 *     This is runtime exception that indicates error occurred during the creation of bean.</p>
 *     <pre class="code">
 *         &#064;Bean("myBean")
 *         public MyBean createMyBean(DependencyBean dependency) {
 *             return new MyBean(dependency);
 *         }
 *     </pre>
 * </p>
 *
 * <p>
 *
 * The Bean annotation is a key part of the Inversion of Control (IoC) principle realized.
 * It frees from manually controlling the lifecycle and dependencies of objects,
 *                          making code cleaner and easier to test and maintain.
 * @see Configuration
 * @see svydovets.core.context.ApplicationContext
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {
    String value() default "";
}
