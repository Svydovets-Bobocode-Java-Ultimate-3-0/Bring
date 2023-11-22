package svydovets.web;

import java.util.*;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.web.dto.RequestInfoHolder;

public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext
    implements WebApplicationContext {

  private final Map<String, RequestInfoHolder> getMethods;
  private final Map<String, RequestInfoHolder> postMethods;
  private final Map<String, RequestInfoHolder> putMethods;
  private final Map<String, RequestInfoHolder> deleteMethods;
  private final Map<String, RequestInfoHolder> patchMethods;

  public AnnotationConfigWebApplicationContext(String basePackage) {
    super(basePackage);

    RestMethodFiller methodFiller = new RestMethodFiller();
    methodFiller.fill(beanFactory.getBeans());

    getMethods = methodFiller.getGetMethods();
    postMethods = methodFiller.getPostMethods();
    putMethods = methodFiller.getPutMethods();
    deleteMethods = methodFiller.getDeleteMethods();
    patchMethods = methodFiller.getPatchMethods();
  }

  @Override
  public Set<String> getMethodPatterns(MethodNameEnum methodNameEnum) {
    return switch (methodNameEnum) {
      case GET -> getMethods.keySet();
      case POST -> postMethods.keySet();
      case PUT -> putMethods.keySet();
      case PATCH -> patchMethods.keySet();
      case DELETE -> deleteMethods.keySet();
    };
  }

  @Override
  public RequestInfoHolder getRequestInfoHolder(MethodNameEnum methodNameEnum, String path) {
    return switch (methodNameEnum) {
      case GET -> getMethods.get(path);
      case POST -> postMethods.get(path);
      case PUT -> putMethods.get(path);
      case PATCH -> patchMethods.get(path);
      case DELETE -> deleteMethods.get(path);
    };
  }
}
