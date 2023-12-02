package svydovets.web;

import svydovets.core.context.ApplicationContext;
import svydovets.web.dto.RequestInfoHolder;

import java.util.Set;

/**
 * <p>
 *     WebApplicationContext specifically designed for web applications.
 *     In a web application need to handle HTTP requests. Requests come with various HTTP method details,
 *     path of request, etc.
 * </p>
 *
 * <p>
 *     WebApplicationContext provides methods to access this information in a structured way.
 *     It also provides information about how to handle HTTP requests.
 *     This includes details which bean should handle request to certain path, or what to do when request is received.
 * </p>
 *
 * <p>
 *     WebApplicationContext also takes control of routing the HTTP requests to the right beans.
 *     When request comes in, WebApplicationContext looks at the details of request,
 *     finds right bean to handle it, and passes request details to that bean.
 *     This way, the WebApplicationContext frees from manually managing routing of HTTP requests.
 * </p>
 * @see ApplicationContext
 */
public interface WebApplicationContext extends ApplicationContext {

    /**
     * Get patterns of the methods for specific HTTP method.
     *
     * @param httpMethod HTTP method for which patterns should be retrieved
     * @return a set of strings representing the method patterns for the specified HTTP method
     */
    Set<String> getMethodPatterns(HttpMethod httpMethod);

    /**
     * Get RequestInfoHolder for specific HTTP method and path.
     *
     * @param httpMethod the HTTP method for which RequestInfoHolder should be retrieved
     * @param path path for which the RequestInfoHolder should be retrieved
     * @return the requestInfoHolder fospecified HTTP method and path
     */
    RequestInfoHolder getRequestInfoHolder(HttpMethod httpMethod, String path);

}
