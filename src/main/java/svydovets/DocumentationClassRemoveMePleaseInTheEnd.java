package svydovets;

public class DocumentationClassRemoveMePleaseInTheEnd {

    // todo: 0) Просканувати базовий пакет і для всіх кандидатів бінів створити власний bean definition(рецепт створення біна) - todo: 1
// Кандидат біна - це клас з аннотацією @Component або @Bean
// Ключ в контексті бінів і контексті bean definitions має бути однаковий (назва класа або значення з анотацій вище) => BeanNameResolver class
// todo: 1) Create bean definitions based on beans metadata
//  todo: 1.1) BeanDefinition beanDefinition = new BeanDefinition(bean.class)
//  todo: 1.2) Fields:   String: beanName, Class<?>: beanClass, List<Class<?>>: constructorCandidates,
//                      List<String>: injectFieldCandidates, Scope: scope,
//                      boolean: isPrimary, Constructor<?> primaryConstructor,
//                      boolean isBeanFromConfigClass, Class<?> configClass,
//                      boolean isConfigClass(для кофіга через класс, не точно),
//                      Method initMethodOfBeanFromConfigClass(для кофіга через класс, не точно)
//  todo: 1.3) @Autowired можливий 3 способами:
//      1) Конструктор(етап створення)
//      2) Сеттер(етап ініціалізації)
//      3) Поле(етап ініціалізації)
//      1.3.1 При створені bean definition перевіряємо наявність конструктора з @Autowired
//      Case 1: конструктора з @Autowired є
//      - Якщо таких конструкторів більше 1го, кидаємо помилку "BeanCreationException"
//      - Якщо конструктор 1, параметри конструктора зберігаємо в список "constructorCandidates", а конструктор в "primaryConstructor"
//      Case 2: конструктора з @Autowired немає
//      - Перевіярємо наявність дефолтного конструктора:
//      - Case 2.1: Якщо є - ініціалізуємо beanDefinition. Список "constructorCandidates" пусте (інші поля в процесі ініціалізації)
//      - Case 2.2: Якщо немає - кидаємо помилку "NoDefaultConstructorException" (т.к. не зможемо створити бін)
//      1.3.2 - 1.3.3 При створені bean definition перевіряємо наявність @Autowired над полями.
//      Зберігаємо їх назву в список "injectFieldCandidates"
// todo: 2) Create bean based on bean definition
//      todo: 2.1) Перевіряємо список "constructorCandidates":
//          Case 1: Список пустий - переходимо до пункту todo: 2.2
//          Case 2: Список непустий - переходимо до пункту todo: 2.1.1
//          todo: 2.1.1) Перевіряємо наявність кожного елемента "constructorCandidates" в контексті бінів:
//              Case 1: Такого аргумента немає в контексті: Перевіряємо, що наявний bean definition для цього аргументу:
//                  Case 1.1: bean definition є - переходимо до пункту todo: 2 для цього аргументу (рекурсивно)
//                  Case 1.2: bean definition немає - кидаємо "NoSuchBeanDefinitionException"
//              Case 2: Такий аргумет є в контексті: Переходимо до перевірки наступного аргумента todo: 2.1.1
//      todo: 2.2) Створюємо інстанс біна через "primaryConstructor" і переходимо до пункту todo: 3
// todo: 3) beanPostProcessor.beforeInit(String beanName, Object bean)
//      todo: 3.1) Опрацювати і повернути бін
// todo: 4) initBean() => autowire dependencies(список "injectFieldCandidates"), set property values
//      todo: 4.1) Ін'єкція залежностей для полів з @Autowired (список injectFieldCandidates)
//                  Class<?> beanClass = bean.getClass()
//                  for (String fieldName : injectFieldCandidates) {
//                      Field autowireCandidateField = beanClass.getDeclaredField(fieldName);
//                      autowireCandidateField.setAccessible(true);
//                      autowireCandidateField.set(bean, context.getBean(autowireCandidateField.getName()))
//      todo: 4.2) Ін'єкція залежностей для методів з @Autowired (без списка)
//                  Class<?> beanClass = bean.getClass()
//                  for (Method method : beanClass.getDeclaredMethods()) {
//                      if (method.isAnnotationPresent(Autowired.class)) {
//                          Parameter[] parameters = method.getParameters();
//                          Class<?>[] autowireParameterTypes = new Class[parameters.length];
//                          for (int i = 0; i < parameters.length; i++) {
//                              Object autowireCandidate = context.getBean(param.getType() - Зберегти в масив
//                              autowireParameterTypes[i] = autowireCandidate
//                          }
//                          method.invoke(bean, autowireParameters)
//                      }
// todo: 5) beanPostProcessor.afterInit(String beanName, Object bean)
//      todo: 5.1) Опрацювати і повернути бін, зберігти в контексті бінів

}
