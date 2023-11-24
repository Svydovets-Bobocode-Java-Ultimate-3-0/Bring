package svydovets.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
import java.util.stream.Collectors;

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
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);

    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        super.service(req, res);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            processRequest(req, MethodNameEnum.GET);
        } catch (Exception e) {
            //todo: if this value is null, throws exception
        }
    }

    // todo: Remove "throws Exception"
    private void processRequest(HttpServletRequest req, MethodNameEnum httpMethodName) throws Exception {
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
        methodToInvoke.invoke(controller, requestArguments);
    }

    private String getControllerMethodPath(String requestPath, MethodNameEnum httpMethodName) {
        Set<String> patternPath = webApplicationContext.getMethodPatterns(httpMethodName);
        return pathFinder.find(requestPath, patternPath);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Set<String> patternPaths = webApplicationContext.getMethodPatterns(MethodNameEnum.POST);

        String foundPattern = pathFinder.find(req.getPathInfo(), patternPaths);

        RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(MethodNameEnum.POST, foundPattern);
        //todo: if this value is null, throws exception
    }

    @Override
    protected long getLastModified(HttpServletRequest req) {
        return super.getLastModified(req);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doHead(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doOptions(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doTrace(req, resp);
    }
}
