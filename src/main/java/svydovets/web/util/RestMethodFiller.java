package svydovets.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import svydovets.web.annotation.DeleteMapping;
import svydovets.web.annotation.GetMapping;
import svydovets.web.annotation.PatchMapping;
import svydovets.web.annotation.PostMapping;
import svydovets.web.annotation.PutMapping;
import svydovets.web.annotation.RequestMapping;
import svydovets.web.annotation.RestController;
import svydovets.web.dto.RequestInfoHolder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for filling maps of REST methods categorized by HTTP verb
 * from a set of beans annotated with {@link RestController}.
 */
public class RestMethodFiller {

    private static final Logger log = LoggerFactory.getLogger(RestMethodFiller.class);

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
        log.trace("Call fill() rest controller");

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
        log.trace("Call fillMethods({})", entry);
        String key = entry.getKey();
        Class<?> beanType = entry.getValue().getClass();
        String controllerPath = getControllerPath(beanType);
        for (Method method : beanType.getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(GetMapping.class).value();
                log.trace("Put GET method context by path: {}", path);

                getMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PostMapping.class).value();
                log.trace("Put POST method context by path: {}", path);

                postMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PutMapping.class).value();
                log.trace("Put PUT method context by path: {}", path);

                putMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(DeleteMapping.class).value();
                log.trace("Put DELETE method context by path: {}", path);

                deleteMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            } else if (method.isAnnotationPresent(PatchMapping.class)) {
                String path = controllerPath + method.getDeclaredAnnotation(PatchMapping.class).value();
                log.trace("Put PATCH method context by path: {}", path);

                patchMethods.put(path, RequestInfoHolderCreator.create(key, beanType, method));
            }
        }
    }

    private static String getControllerPath(Class<?> beanType) {
        log.trace("Call getControllerPath({})", beanType);
        if (!beanType.isAnnotationPresent(RequestMapping.class)) {
            return "";
        }

        return beanType.getDeclaredAnnotation(RequestMapping.class).value();
    }

}
