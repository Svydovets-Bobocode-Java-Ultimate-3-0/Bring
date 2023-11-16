package svydovets.core.context.injector;

import java.lang.reflect.Field;

public class MapOfBeansInjector extends AbstractInjector {
  @Override
  public void inject(InjectorConfig config) {
    Object bean = config.getBean();
    Field field = config.getBeanField();

    Class<?> autowireCandidateType = retrieveAutowireCandidateType(field);
    var mapOfBeansForInjection = retrieveFieldValue(bean, field);
    var mapOfBeansToInject = config.getBeanOfTypeReceiver().apply(autowireCandidateType);

    if (mapOfBeansForInjection == null) {
      // Initialize map logic
      setDependency(bean, field, mapOfBeansToInject);
    } else {
      // todo: Implement BR-6
      // todo: CREATE NEW MAP IMPLEMENTATION AND SET TO FIELD!
      setDependency(bean, field, mapOfBeansToInject);
    }
  }
}
