package svydovets.web;

import svydovets.core.context.ApplicationContext;
import svydovets.web.dto.RequestInfoHolder;

import java.util.Set;

public interface WebApplicationContext extends ApplicationContext {

    Set<String> getMethodPatterns(HttpMethod httpMethod);

    RequestInfoHolder getRequestInfoHolder(HttpMethod httpMethod, String path);

}
