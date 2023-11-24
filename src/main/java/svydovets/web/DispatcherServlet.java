package svydovets.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;
import svydovets.web.path.RequestInfo;
import svydovets.web.path.RequestInfoCreator;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {

    private final WebApplicationContext webApplicationContext;
    private final PathFinder pathFinder;
    private final WebInvocationHandler webInvocationHandler;

    public DispatcherServlet(String basePackage) {
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(basePackage);
        this.pathFinder = new PathFinderImpl();
        this.webInvocationHandler = new WebInvocationHandler();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext().setAttribute("WebApplicationContext", webApplicationContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, MethodNameEnum.GET);
        } catch (Exception e) {

        }
        // todo: Process "result"
    }

    // todo: Remove "throws Exception"
    private Object processRequest(HttpServletRequest req, MethodNameEnum httpMethodName) throws Exception {
        String requestPath = req.getPathInfo();

        RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(httpMethodName, requestPath);
        Class<?> controllerType = requestInfoHolder.getClassType();
        Object controller = webApplicationContext.getBean(requestInfoHolder.getClassName(), controllerType);
        // todo: Move logic of getting method by name to additional method
        Method methodToInvoke = controllerType.getDeclaredMethod(requestInfoHolder.getMethodName(), requestInfoHolder.getParameterTypes());

        String controllerMethodPath = getControllerMethodPath(requestPath, httpMethodName);
        req.setAttribute(RequestDispatcher.INCLUDE_REQUEST_URI, controllerMethodPath);


        RequestInfo requestInfo = RequestInfoCreator.create(req);
        Object[] requestArguments = webInvocationHandler.invoke(methodToInvoke, requestInfo);
        return methodToInvoke.invoke(controller, requestArguments);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, MethodNameEnum.POST);
        } catch (Exception e) {

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, MethodNameEnum.PUT);
        } catch (Exception e) {
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, MethodNameEnum.DELETE);
        } catch (Exception e) {
        }
    }

    private String getControllerMethodPath(String requestPath, MethodNameEnum httpMethodName) {
        Set<String> patternPath = webApplicationContext.getMethodPatterns(httpMethodName);
        return pathFinder.find(requestPath, patternPath);
    }
}
