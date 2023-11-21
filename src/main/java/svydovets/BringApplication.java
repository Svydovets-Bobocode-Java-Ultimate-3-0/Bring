package svydovets;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import svydovets.core.context.ApplicationContext;
import svydovets.web.DispatcherServlet;

import java.io.File;

public class BringApplication {

    public static ApplicationContext run(Class<?> baseClass) {
        try {
           return configureTomcat(baseClass);
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
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
        String urlPattern = "/test";

        DispatcherServlet dispatcherServlet = new DispatcherServlet(baseClass.getPackageName());
        tomcat.addServlet(contextPath, servletName, dispatcherServlet);
        context.addServletMappingDecoded(urlPattern, servletName);
        tomcat.start();
        tomcat.getServer().await();
        return (ApplicationContext) dispatcherServlet.getServletContext().getAttribute("WebApplicationContext");
    }
}
