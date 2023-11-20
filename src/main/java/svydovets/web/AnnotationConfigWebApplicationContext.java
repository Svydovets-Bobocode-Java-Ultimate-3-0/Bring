package svydovets.web;

import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.web.dto.RequestInfoHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext
    implements WebApplicationContext {

  public AnnotationConfigWebApplicationContext(String basePackage) {
    super(basePackage);
  }

  private Map<String, RequestInfoHolder> getMethods = new HashMap<>();
  private Map<String, RequestInfoHolder> postMethods = new HashMap<>();

  public Set<String> getMethodPatterns(MethodNameEnum methodNameEnum) {
    return switch (methodNameEnum) {
      case GET -> getMethods.keySet();
      case POST -> postMethods.keySet();
      default -> getMethods.keySet();
    };
  }

  public RequestInfoHolder getRequestInfoHolder(MethodNameEnum methodNameEnum, String path) {
    return switch (methodNameEnum) {
      case GET -> getMethods.get(path);
      case POST -> postMethods.get(path);
      default -> getMethods.get(path);
    };
  }
}
