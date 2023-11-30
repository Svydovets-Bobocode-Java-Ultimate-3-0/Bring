package svydovets.web;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.util.RestMethodFiller;

public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext
    implements WebApplicationContext {

  private static final Logger log = LoggerFactory.getLogger(AnnotationConfigWebApplicationContext.class);

  private final Map<String, RequestInfoHolder> getMethods;

  private final Map<String, RequestInfoHolder> postMethods;

  private final Map<String, RequestInfoHolder> putMethods;

  private final Map<String, RequestInfoHolder> deleteMethods;

  private final Map<String, RequestInfoHolder> patchMethods;

  public AnnotationConfigWebApplicationContext(String basePackage) {
    super(basePackage);

    RestMethodFiller methodFiller = new RestMethodFiller();
    methodFiller.fill(beanFactoryImpl.getBeans());

    getMethods = methodFiller.getGetMethods();
    postMethods = methodFiller.getPostMethods();
    putMethods = methodFiller.getPutMethods();
    deleteMethods = methodFiller.getDeleteMethods();
    patchMethods = methodFiller.getPatchMethods();
  }

  @Override
  public Set<String> getMethodPatterns(HttpMethod httpMethod) {
    log.trace("Call getMethodPatterns({})", httpMethod);

    return switch (httpMethod) {
      case GET -> getMethods.keySet();
      case POST -> postMethods.keySet();
      case PUT -> putMethods.keySet();
      case PATCH -> patchMethods.keySet();
      case DELETE -> deleteMethods.keySet();
    };
  }

  @Override
  public RequestInfoHolder getRequestInfoHolder(HttpMethod httpMethod, String path) {
    log.trace("Call getRequestInfoHolder({}, {})", httpMethod, path);

    return switch (httpMethod) {
      case GET -> getMethods.get(path);
      case POST -> postMethods.get(path);
      case PUT -> putMethods.get(path);
      case PATCH -> patchMethods.get(path);
      case DELETE -> deleteMethods.get(path);
    };
  }
}
