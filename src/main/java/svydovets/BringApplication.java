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

public class BringApplication {
    private static final Logger log = LoggerFactory.getLogger(BringApplication.class);
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_SERVLET_NAME = "DispatcherServlet";
    private static final String DEFAULT_SERVLET_URL_PATTERN = "/";
    private static final String DEFAULT_SERVLET_CONTEXT_PATH = "";

    public static ApplicationContext run(Class<?> baseClass) {
        try {
            return configureTomcat(baseClass);
        } catch (LifecycleException e) {
            String errorMessage = String.format(ErrorMessageConstants.ERROR_STARTING_EMBEDDED_TOMCAT, baseClass.getName());
            log.error(errorMessage);

            throw new TomcatStartingException(errorMessage, e);
        }
    }

    private static ApplicationContext configureTomcat(Class<?> baseClass) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("java.io.tmpdir");
//        tomcat.setBaseDir("temp");
        tomcat.setPort(DEFAULT_PORT);
        tomcat.getConnector();

//        String docBase = new File(".").getAbsolutePath();
        String docBase = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        Context context = tomcat.addContext(DEFAULT_SERVLET_CONTEXT_PATH, docBase);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(baseClass.getPackageName());
        tomcat.addServlet(DEFAULT_SERVLET_CONTEXT_PATH, DEFAULT_SERVLET_NAME, dispatcherServlet);
        context.addServletMappingDecoded(DEFAULT_SERVLET_URL_PATTERN, DEFAULT_SERVLET_NAME);
        tomcat.start();
        tomcat.getServer().await();
        return (ApplicationContext) dispatcherServlet.getServletContext().getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT);
    }
}
