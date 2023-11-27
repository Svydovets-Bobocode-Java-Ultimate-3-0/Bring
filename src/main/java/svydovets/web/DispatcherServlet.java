package svydovets.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import svydovets.exception.RequestProcessingException;
import svydovets.util.ErrorMessageConstants;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.dto.ResponseEntity;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class DispatcherServlet extends HttpServlet {

    public static final String CONTROLLER_REDIRECT_REQUEST_PATH = "controllerRedirectRequestPath";
    public static final String WEB_APPLICATION_CONTEXT = "webApplicationContext";
    private static final PathFinder pathFinder = new PathFinderImpl();
    private static final MethodArgumentResolver methodArgumentResolver = new MethodArgumentResolver();
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
        processRequest(req, resp, HttpMethod.GET);
    }


    private void processResponseEntity(HttpServletResponse resp, Object responseObject) throws IOException {

        if (responseObject instanceof ResponseEntity<?> responseEntity){
            //get info from ResponseEntity
            int status = responseEntity.getStatus().getStatus();
            Map<String, String> headers = responseEntity.getHeaders().getHeaders();
            Object body = responseEntity.getBody();

            //set info from ResponseEntity to response
            resp.setStatus(status);
            headers.forEach(resp::setHeader);
            String jsonBody = ServletWebRequest.objectMapper.writeValueAsString(body);

            //v1
            resp.getWriter().write(jsonBody);
            //v2
//            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()));
//            printWriter.println(jsonBody);

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, HttpMethod.POST);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, HttpMethod.PUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp, HttpMethod.DELETE);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) {
        try {
            String requestPath = req.getServletPath();

            RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(httpMethod, requestPath);

            saveControllerRedirectRequestPathAsAttribute(req, httpMethod, requestPath);

            Class<?> controllerType = requestInfoHolder.getClassType();
            Object controller = webApplicationContext.getBean(requestInfoHolder.getClassName(), controllerType);

            Method methodToInvoke = controllerType.getDeclaredMethod(requestInfoHolder.getMethodName(), requestInfoHolder.getParameterTypes());

            ServletWebRequest servletWebRequest = new ServletWebRequest(req, resp);
            Object[] resolvedRequestArguments = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);
            Object result = methodToInvoke.invoke(controller, resolvedRequestArguments);

            processRequestResult(resp, result);
        } catch (Exception e) {
            throw new RequestProcessingException(
                    String.format(ErrorMessageConstants.REQUEST_PROCESSING_ERROR, httpMethod.name(), req.getServletPath()),
                    e
            );
        }
    }

    private void saveControllerRedirectRequestPathAsAttribute(HttpServletRequest req, HttpMethod httpMethod, String requestPath) {
        String controllerMethodPath = getControllerMethodPath(requestPath, httpMethod);
        req.setAttribute(CONTROLLER_REDIRECT_REQUEST_PATH, controllerMethodPath);
    }

    private void processRequestResult(HttpServletResponse response, Object result) throws Exception {
        if (result != null) {
            if (result instanceof ResponseEntity<?> responseEntity){
                processResponseEntity(response, responseEntity);
            } else {
                String json = ServletWebRequest.objectMapper.writeValueAsString(result);
                response.getWriter().write(json);
            }
        }
    }

    private String getControllerMethodPath(String requestPath, HttpMethod httpMethodName) {
        Set<String> patternPath = webApplicationContext.getMethodPatterns(httpMethodName);
        return pathFinder.find(requestPath, patternPath);
    }
}
