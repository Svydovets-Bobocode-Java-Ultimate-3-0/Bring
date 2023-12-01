package svydovets;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.ApplicationContext;
import svydovets.exception.TomcatStartingException;
import svydovets.util.ErrorMessageConstants;
import svydovets.web.DispatcherServlet;

import java.io.File;

public class BringApplication {
    private static final Logger log = LoggerFactory.getLogger(BringApplication.class);

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
        // Solution 1
        tomcat.setPort(8080);
        tomcat.getConnector();

        String contextPath = "";
//        String docBase = new File(".").getAbsolutePath();
        String docBase = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        Context context = tomcat.addContext(contextPath, docBase);

        String servletName = "DispatcherServlet";
        String urlPattern = "/";

        DispatcherServlet dispatcherServlet = new DispatcherServlet(baseClass.getPackageName());
        tomcat.addServlet(contextPath, servletName, dispatcherServlet);
        context.addServletMappingDecoded(urlPattern, servletName);
        tomcat.start();
        tomcat.getServer().await();
        return (ApplicationContext) dispatcherServlet.getServletContext().getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT);
    }
}
