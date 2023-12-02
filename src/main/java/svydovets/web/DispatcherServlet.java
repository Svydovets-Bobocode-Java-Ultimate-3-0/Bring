package svydovets.web;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.util.ErrorMessageConstants;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.dto.ResponseEntity;
import svydovets.web.exception.RequestProcessingException;
import svydovets.web.path.PathFinder;
import svydovets.web.path.PathFinderImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * The DispatcherServlet is a key component in the Bring Web framework, responsible for handling incoming HTTP requests and
 * dispatching them to the appropriate controllers for processing.
 * It plays a central role in the request-response processing flow of application.
 *
 * <p>This servlet handles HTTP requests by dispatching them to the appropriate controller methods
 * based on the request path and HTTP method.</p>
 *
 * <p>Purpose:</p>
 * <ul>
 *     <li>Request Handling: The primary purpose of the DispatcherServlet is to manage the flow of incoming HTTP requests. It acts as a front controller, receiving requests and deciding how to process them.</li>
 *     <li>Request Dispatching: The DispatcherServlet dispatches requests to the appropriate controller based on the configuration provided. It examines the request URL and delegates the request to the corresponding controller, known as a @RestController in the Spring MVC framework.</li>
 *     <li>Interception: It provides a mechanism for interceptors to be executed before and after the actual handling of a request. Interceptors can perform tasks such as logging, security checks, or modifying the model.</li>
 * </ul>
 *
 * <p>Key Features:</p>
 * <ul>
 *   <li>Supports common HTTP methods: GET, POST, PUT, PATCH and DELETE.</li>
 *   <li>Utilizes annotations for configuring the base package of controller classes.</li>
 *   <li>Handles request processing, method invocation, and response generation.</li>
 * </ul>
 *
 * @see WebApplicationContext
 * @see PathFinder
 * @see MethodArgumentResolver
 */
public class DispatcherServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    /**
     * Attribute key for storing controller redirect request path
     */
    public static final String CONTROLLER_REDIRECT_REQUEST_PATH = "controllerRedirectRequestPath";
    /**
     * Attribute key for storing the web application context.
     */
    public static final String WEB_APPLICATION_CONTEXT = "webApplicationContext";
    /**
     * Implementation of the path finder interface for locating controller method paths
     */
    private static final PathFinder pathFinder = new PathFinderImpl();
    /**
     * Resolves method arguments for controller method invocation
     */
    private static final MethodArgumentResolver methodArgumentResolver = new MethodArgumentResolver();
    /**
     * Used for creating the web application context
     */
    private static final String FAVICON_PATH = "/favicon.ico";
    private static final String PATCH_METHOD = "PATCH";
    private final WebApplicationContext webApplicationContext;


    /**
     * Constructs a new DispatcherServlet with the specified base package.
     * The provided base package is used for creating a {@link AnnotationConfigWebApplicationContext} scanning not only controllers but all candidate beans for inclusion in the
     * application context
     *
     * @param basePackage base package for scanning all bean candidates
     * @see AnnotationConfigWebApplicationContext
     */
    public DispatcherServlet(String basePackage) {
        this.webApplicationContext = new AnnotationConfigWebApplicationContext(basePackage);
    }

    /**
     * Initializes the servlet and sets the WebApplicationContext as a servlet context attribute.
     *
     * @param config the ServletConfig object containing servlet configuration
     * @throws ServletException if an error occurs during servlet initialization
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext().setAttribute(WEB_APPLICATION_CONTEXT, webApplicationContext);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();

        if (!PATCH_METHOD.equals(method)) {
            super.service(req, resp);
        } else {
            doPatch(req, resp);
        }
    }

    /**
     * Handles GET requests by processing the request and invoking the appropriate controller method.
     *
     * @param req  the HttpServletRequest object representing the client request
     * @param resp the HttpServletResponse object representing the response to be sent
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("The GET method was invoked");

        processRequest(req, resp, HttpMethod.GET);
    }

    /**
     * Handles POST requests by processing the request and invoking the appropriate controller method.
     *
     * @param req  the HttpServletRequest object representing the client request
     * @param resp the HttpServletResponse object representing the response to be sent
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("The POST method was invoked");

        processRequest(req, resp, HttpMethod.POST);
    }

    /**
     * Handles PUT requests by processing the request and invoking the appropriate controller method.
     *
     * @param req  the HttpServletRequest object representing the client request
     * @param resp the HttpServletResponse object representing the response to be sent
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("The PUT method was invoked");

        processRequest(req, resp, HttpMethod.PUT);
    }

    /**
     * Handles DELETE requests by processing the request and invoking the appropriate controller method.
     *
     * @param req  the HttpServletRequest object representing the client request
     * @param resp the HttpServletResponse object representing the response to be sent
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("The DELETE method was invoked");

        processRequest(req, resp, HttpMethod.DELETE);
    }

    /**
     * Handles PATCH requests by processing the request and invoking the appropriate controller method.
     *
     * @param req  the HttpServletRequest object representing the client request
     * @param resp the HttpServletResponse object representing the response to be sent
     * @throws ServletException if an error occurs during request processing
     * @throws IOException      if an I/O error occurs
     */
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("The PATCH method was invoked");

        processRequest(req, resp, HttpMethod.PATCH);
    }

    /**
     * Processes the response entity and writes it to the HttpServletResponse.
     * Method takes an object that represents the response entity, extracts relevant information such as status, headers, and body,
     * and then constructs an HTTP response by setting the status and headers before writing the serialized JSON body
     * to the response. It is a key component in converting the output of a controller method into a well-formed HTTP response.
     *
     * @param resp           the HttpServletResponse object representing the response to be sent
     * @param responseObject the object representing the response entity
     * @throws IOException if an I/O error occurs
     */
    private void processResponseEntity(HttpServletResponse resp, Object responseObject) throws IOException {
        if (responseObject instanceof ResponseEntity<?> responseEntity) {
            int status = responseEntity.getHttpStatus().getStatus();
            Map<String, String> headers = responseEntity.getHttpHeaders().getHeaders();
            Object body = responseEntity.getBody();

            resp.setStatus(status);
            headers.forEach(resp::setHeader);
            String jsonBody = ServletWebRequest.objectMapper.writeValueAsString(body);

            resp.getWriter().write(jsonBody);
        }
    }

    /**
     * Method is responsible for handling an incoming HTTP request by delegating it to the appropriate controller for processing.
     * It encapsulates the entire request processing logic, from extracting information about the request to invoking the appropriate controller method and handling exceptions.
     *
     * @param req        the HttpServletRequest object representing the client request
     * @param resp       the HttpServletResponse object representing the response to be sent
     * @param httpMethod the HttpMethod representing the HTTP method of the request
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp, HttpMethod httpMethod) {
        try {
            String requestPath = req.getServletPath();
            if (isNotFaviconRequest(requestPath)) {

                String controllerRedirectRequestPath = saveControllerRedirectRequestPathAsAttribute(req, httpMethod, requestPath);

                RequestInfoHolder requestInfoHolder = webApplicationContext.getRequestInfoHolder(httpMethod, controllerRedirectRequestPath);

                Class<?> controllerType = requestInfoHolder.getClassType();
                Object controller = webApplicationContext.getBean(requestInfoHolder.getClassName(), controllerType);

                Method methodToInvoke = controllerType.getDeclaredMethod(requestInfoHolder.getMethodName(), requestInfoHolder.getParameterTypes());

                ServletWebRequest servletWebRequest = new ServletWebRequest(req, resp);
                Object[] resolvedRequestArguments = methodArgumentResolver.resolveArguments(methodToInvoke, servletWebRequest);
                Object result = methodToInvoke.invoke(controller, resolvedRequestArguments);

                processRequestResult(resp, result);
            }
        } catch (Exception e) {
            String errorMessage = String.format(ErrorMessageConstants.REQUEST_PROCESSING_ERROR, httpMethod.name(), req.getServletPath());
            log.error(errorMessage);

            throw new RequestProcessingException(errorMessage, e);
        }
    }

    /**
     * Checks if the given request path does not correspond to the favicon.ico file.
     *
     * <p>The method evaluates whether the provided {@code requestPath} is different from the path commonly associated
     * with favicon.ico, considering the case-insensitivity of URLs. If the request path is not null and is not equal to
     * the favicon path (ignoring case), the method returns {@code true}, indicating that the request is not for the
     * favicon.ico file. Otherwise, it returns {@code false}.</p>
     *
     * <p>This method is particularly useful in web applications where it is necessary to identify requests for the favicon
     * file to exclude them from certain processing, such as logging or authentication checks. The favicon.ico file is
     * a standard icon file commonly used as a website's icon, and browsers automatically request it. Excluding it from
     * certain processing can help optimize and streamline request handling.</p>
     *
     * @param requestPath the request path
     * @return true if the request is not for favicon.ico, false otherwise
     */

    private boolean isNotFaviconRequest(String requestPath) {
        return requestPath != null && !FAVICON_PATH.equalsIgnoreCase(requestPath);
    }

    /**
     * Saves the controller redirect request path as an attribute in the HttpServletRequest.
     *
     * @param req         the HttpServletRequest object representing the client request
     * @param httpMethod  the HttpMethod representing the HTTP method of the request
     * @param requestPath the request path
     * @return the controller method path
     */
    private String saveControllerRedirectRequestPathAsAttribute(HttpServletRequest req, HttpMethod httpMethod, String requestPath) {
        String controllerMethodPath = getControllerMethodPath(requestPath, httpMethod);
        req.setAttribute(CONTROLLER_REDIRECT_REQUEST_PATH, controllerMethodPath);
        return controllerMethodPath;
    }

    /**
     * Processes the result of the controller method and assembles the final response by writing the obtained result to the HttpServletResponse in JSON format.
     *
     * @param response the HttpServletResponse object representing the response to be sent
     * @param result   the result of the controller method
     * @throws Exception if an error occurs during processing
     */
    private void processRequestResult(HttpServletResponse response, Object result) throws Exception {
        if (result != null) {
            if (result instanceof ResponseEntity<?> responseEntity) {
                processResponseEntity(response, responseEntity);
            } else {
                String json = ServletWebRequest.objectMapper.writeValueAsString(result);
                response.getWriter().write(json);
            }
        }
    }

    /**
     * Extract the controller method redirect path using the {@link PathFinder} implementation
     *
     * @param requestPath    the request path
     * @param httpMethodName the HTTP method name
     */
    private String getControllerMethodPath(String requestPath, HttpMethod httpMethodName) {
        Set<String> patternPath = webApplicationContext.getMethodPatterns(httpMethodName);
        return pathFinder.find(requestPath, patternPath);
    }
}
