package svydovets.web.util;

import svydovets.web.MethodNameEnum;
import svydovets.web.annotation.*;
import svydovets.web.dto.RequestInfoHolder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for filling maps of REST methods categorized by HTTP verb
 * from a set of beans annotated with {@link RestController}.
 */
public class RestMethodFiller {

    private final Map<String, RequestInfoHolder> getMethods = new HashMap<>();
    private final Map<String, RequestInfoHolder> postMethods = new HashMap<>();
    private final Map<String, RequestInfoHolder> putMethods = new HashMap<>();
    private final Map<String, RequestInfoHolder> deleteMethods = new HashMap<>();
    private final Map<String, RequestInfoHolder> patchMethods = new HashMap<>();

    /**
     * Fills maps of REST methods based on the provided beans.
     *
     * @param beans beans that were found by scanner.
     */
    public void fill(Map<String, Object> beans) {
        beans.entrySet().stream()
                .filter(entry -> entry.getValue().getClass().isAnnotationPresent(RestController.class))
                .forEach(this::fillMethods);
    }


    /**
     * Returns the map of GET methods.
     *
     * @return The map of GET methods.
     */
    public Map<String, RequestInfoHolder> getGetMethods() {
        return getMethods;
    }

    /**
     * Returns the map of POST methods.
     *
     * @return The map of POST methods.
     */
    public Map<String, RequestInfoHolder> getPostMethods() {
        return postMethods;
    }

    /**
     * Returns the map of PUT methods.
     *
     * @return The map of PUT methods.
     */
    public Map<String, RequestInfoHolder> getPutMethods() {
        return putMethods;
    }

    /**
     * Returns the map of DELETE methods.
     *
     * @return The map of DELETE methods.
     */
    public Map<String, RequestInfoHolder> getDeleteMethods() {
        return deleteMethods;
    }

    /**
     * Returns the map of PATCH methods.
     *
     * @return The map of PATCH methods.
     */
    public Map<String, RequestInfoHolder> getPatchMethods() {
        return patchMethods;
    }

    private void fillMethods(Map.Entry<String, Object> entry) {
        String key = entry.getKey();
        Class<?> beanType = entry.getValue().getClass();
        String controllerPath = getControllerPath(beanType);
        for (Method method : beanType.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(GetMapping.class).value();

                getMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PostMapping.class).value();

                postMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PutMapping.class).value();

                putMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(DeleteMapping.class).value();

                deleteMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PatchMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PatchMapping.class).value();

                patchMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
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
