package svydovets.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {

    public static final String CONTROLLER_REDIRECT_REQUEST_PATH = "controllerRedirectRequestPath";
    public static final String WEB_APPLICATION_CONTEXT = "webApplicationContext";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final PathFinder pathFinder = new PathFinderImpl();
    private static final MethodArgumentResolver METHOD_ARGUMENT_RESOLVER = new MethodArgumentResolver();
    private final WebApplicationContext webApplicationContext;

    public DispatcherServlet(String basePackage) {
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(basePackage);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext().setAttribute(WEB_APPLICATION_CONTEXT, webApplicationContext);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, resp, MethodNameEnum.GET);
            processRequestResult(resp, result);
        } catch (Exception e) {
        }
        // todo: Process "result"
    }

    private void processRequestResult(HttpServletResponse resp, Object result) throws IOException {
        if (result != null) {
            MAPPER.writeValue(resp.getOutputStream(), result);
        }
    }

    // todo: Remove "throws Exception"
    private Object processRequest(HttpServletRequest req, HttpServletResponse resp, MethodNameEnum httpMethodName) throws Exception {
        String requestPath = req.getPathInfo();

        RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(httpMethodName, requestPath);
        Class<?> controllerType = requestInfoHolder.getClassType();
        Object controller = webApplicationContext.getBean(requestInfoHolder.getClassName(), controllerType);
        // todo: Move logic of getting method by name to additional method
        Method methodToInvoke = controllerType.getDeclaredMethod(requestInfoHolder.getMethodName(), requestInfoHolder.getParameterTypes());

        // todo: Move to additional method
        String controllerMethodPath = getControllerMethodPath(requestPath, httpMethodName);
        req.setAttribute(RequestDispatcher.INCLUDE_REQUEST_URI, controllerMethodPath);


        ServletWebRequest servletWebRequest = new ServletWebRequest(req, resp);
        Object[] requestArguments = METHOD_ARGUMENT_RESOLVER.invoke(methodToInvoke, servletWebRequest);
        return methodToInvoke.invoke(controller, requestArguments);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, resp, MethodNameEnum.POST);
            processRequestResult(resp, result);
        } catch (Exception e) {

        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, resp, MethodNameEnum.PUT);
            processRequestResult(resp, result);
        } catch (Exception e) {
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = processRequest(req, resp, MethodNameEnum.DELETE);
            processRequestResult(resp, result);
        } catch (Exception e) {
        }
    }

    private String getControllerMethodPath(String requestPath, MethodNameEnum httpMethodName) {
        Set<String> patternPath = webApplicationContext.getMethodPatterns(httpMethodName);
        return pathFinder.find(requestPath, patternPath);
    }
}
