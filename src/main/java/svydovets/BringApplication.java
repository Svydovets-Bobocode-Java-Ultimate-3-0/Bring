package svydovets;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.ApplicationContext;
import svydovets.util.ErrorMessageConstants;
import svydovets.web.DispatcherServlet;
import svydovets.web.exception.TomcatStartingException;

import java.io.File;

/**
 * The {@code BringApplication} class provides a simplified and opinionated way to configure and run an embedded Tomcat
 * server within a Bring framework. It serves as the entry point for starting the
 * application, configuring the embedded Tomcat server, and initializing the application context.
 * <p>
 * The class defines default configurations for the embedded Tomcat server, such as the port, servlet name, servlet URL
 * pattern, servlet context path, and base directory. The {@link BringApplication#run(Class)} method allows developers
 * to start the application by providing the main class as an argument. The embedded Tomcat is configured, and the
 * application context is initialized using a custom implementation of the {@link DispatcherServlet}.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 *   public class MyApplication {
 *       public static void main(String[] args) {
 *           BringApplication.run(MyApplication.class);
 *       }
 *   }
 * }
 * </pre>
 * In this example, the application is started by invoking the {@code BringApplication.run} method with the main class
 * as an argument. The embedded Tomcat is configured, and the application context is initialized.
 * <p>
 * If an exception occurs during the startup process, a custom exception, {@link TomcatStartingException}, is thrown,
 * providing detailed error information. This exception wraps the underlying {@link LifecycleException} thrown by the
 * embedded Tomcat.
 * <p>
 *
 * @see Tomcat
 * @see DispatcherServlet
 * @see ApplicationContext
 */
public class BringApplication {
    private static final Logger log = LoggerFactory.getLogger(BringApplication.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_SERVLET_NAME = "DispatcherServlet";
    private static final String DEFAULT_SERVLET_URL_PATTERN = "/";
    private static final String DEFAULT_SERVLET_CONTEXT_PATH = "";
    private static final String DEFAULT_BASE_DIR = "java.io.tmpdir";

    /**
     * Runs the embedded Tomcat server and initializes the application context based on the provided main class.
     * <p>
     * This method configures the embedded Tomcat with default settings, such as port, servlet name, servlet URL pattern,
     * servlet context path, and base directory. It then starts the Tomcat server and awaits its initialization.
     * <p>
     * If an exception occurs during the startup process, a {@link TomcatStartingException} is thrown, wrapping the
     * underlying {@link LifecycleException}.
     *
     * @param baseClass the main class of the application
     * @return the initialized application context
     * @throws TomcatStartingException if an error occurs during the startup of the embedded Tomcat server
     */
    public static ApplicationContext run(Class<?> baseClass) {
        try {
            return configureTomcat(baseClass);
        } catch (LifecycleException e) {
            String errorMessage = String.format(ErrorMessageConstants.ERROR_STARTING_EMBEDDED_TOMCAT, baseClass.getName());
            log.error(errorMessage);

            throw new TomcatStartingException(errorMessage, e);
        }
    }

    /**
     * Configures the embedded Tomcat server based on default settings and the provided main class.
     * <p>
     * This method sets up the embedded Tomcat with default configurations, including the port, servlet name, servlet URL
     * pattern, servlet context path, and base directory. It then creates a {@link DispatcherServlet} and initializes the
     * application context. The embedded Tomcat is started, and the server awaits initialization.
     *
     * @param baseClass the main class of the application
     * @return the initialized application context
     * @throws LifecycleException if an error occurs during the lifecycle of the embedded Tomcat server
     */
    private static ApplicationContext configureTomcat(Class<?> baseClass) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(DEFAULT_BASE_DIR);
        tomcat.setPort(DEFAULT_PORT);
        tomcat.getConnector();

        String docBase = new File(System.getProperty(DEFAULT_BASE_DIR)).getAbsolutePath();
        Context context = tomcat.addContext(DEFAULT_SERVLET_CONTEXT_PATH, docBase);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(baseClass.getPackageName());
        tomcat.addServlet(DEFAULT_SERVLET_CONTEXT_PATH, DEFAULT_SERVLET_NAME, dispatcherServlet);
        context.addServletMappingDecoded(DEFAULT_SERVLET_URL_PATTERN, DEFAULT_SERVLET_NAME);
        tomcat.start();
        tomcat.getServer().await();
        return (ApplicationContext) dispatcherServlet.getServletContext().getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT);
    }
}