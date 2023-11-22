package svydovets.web;

import java.lang.reflect.Method;
import java.util.*;

import svydovets.core.context.AnnotationConfigApplicationContext;
import svydovets.web.annotation.*;
import svydovets.web.dto.RequestInfoHolder;
import svydovets.web.dto.RequestInfoHolderCreator;

public class AnnotationConfigWebApplicationContext extends AnnotationConfigApplicationContext
    implements WebApplicationContext {

  private final Map<String, RequestInfoHolder> getMethods = new HashMap<>();
  private final Map<String, RequestInfoHolder> postMethods = new HashMap<>();
  private final Map<String, RequestInfoHolder> putMethods = new HashMap<>();
  private final Map<String, RequestInfoHolder> deleteMethods = new HashMap<>();
  private final Map<String, RequestInfoHolder> patchMethods = new HashMap<>();

  public AnnotationConfigWebApplicationContext(String basePackage) {
    super(basePackage);

    beanFactory.getBeans().entrySet().stream()
        .filter(entry -> entry.getValue().getClass().isAnnotationPresent(RestController.class))
        .forEach(
            entry -> {
              Class<?> beanType = entry.getValue().getClass();
              String methodPath = getControllerPath(beanType);
              Method[] declaredMethods = beanType.getDeclaredMethods();
              fillMethods(entry, declaredMethods, methodPath);
            });
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

  private void fillMethods(
      Map.Entry<String, Object> entry, Method[] declaredMethods, String controllerPath) {
    for (Method method : declaredMethods) {
      if (method.isAnnotationPresent(GetMapping.class)) {
        String path = controllerPath + method.getDeclaredAnnotation(GetMapping.class).value();

        getMethods.put(path, RequestInfoHolderCreator.create(entry, method));
      } else if (method.isAnnotationPresent(PostMapping.class)) {
        String path = controllerPath + method.getDeclaredAnnotation(PostMapping.class).value();

        postMethods.put(path, RequestInfoHolderCreator.create(entry, method));
      } else if (method.isAnnotationPresent(PutMapping.class)) {
        String path = controllerPath + method.getDeclaredAnnotation(PutMapping.class).value();

        putMethods.put(path, RequestInfoHolderCreator.create(entry, method));
      } else if (method.isAnnotationPresent(DeleteMapping.class)) {
        String path = controllerPath + method.getDeclaredAnnotation(DeleteMapping.class).value();

        deleteMethods.put(path, RequestInfoHolderCreator.create(entry, method));
      } else if (method.isAnnotationPresent(PatchMapping.class)) {
        String path = controllerPath + method.getDeclaredAnnotation(PatchMapping.class).value();

        patchMethods.put(path, RequestInfoHolderCreator.create(entry, method));
      }
    }
  }

  private static String getControllerPath(Class<?> beanType) {
    if (!beanType.isAnnotationPresent(RequestMapping.class)) {
      return "";
    }

    return beanType.getDeclaredAnnotation(RequestMapping.class).value();
  }
}
