package svydovets.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.web.dto.RequestInfoHolder;

public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext
    implements WebApplicationContext {

  private final Map<String, RequestInfoHolder> getMethods = new HashMap<>();
  private final Map<String, RequestInfoHolder> postMethods = new HashMap<>();

  public AnnotationConfigWebApplicationContext(String basePackage) {
    super(basePackage);
  }

  @Override
  public Set<String> getMethodPatterns(MethodNameEnum methodNameEnum) {
    return switch (methodNameEnum) {
      case GET -> getMethods.keySet();
      case POST -> postMethods.keySet();
      default -> getMethods.keySet();
    };
  }

  @Override
  public RequestInfoHolder getRequestInfoHolder(MethodNameEnum methodNameEnum, String path) {
    return switch (methodNameEnum) {
      case GET -> getMethods.get(path);
      case POST -> postMethods.get(path);
      default -> getMethods.get(path);
    };
  }

}
