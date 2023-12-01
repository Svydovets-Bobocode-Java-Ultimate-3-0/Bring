# Bring Svydovets

In the dynamic landscape of web development, the combination of a robust Web module and an efficient Dependency
Injection system is crucial for crafting maintainable and extensible software. Bring Svydovets brings together the best
of
both worlds, offering a comprehensive framework that simplifies web application development while promoting loose
coupling and code organization through Dependency Injection.

___
# Key Features

## Dependency Injection

The Dependency Injection framework embedded in this project facilitates the management of component dependencies. Say
goodbye to tangled and hard-to-maintain code. With our Dependency Injection system, you can structure your application
in a modular fashion, making it easier to understand, test, and extend. Enjoy the benefits of inversion of control,
dependency injection, and advanced features like lifecycle management and contextual binding.

## Web

Svydovets Web provides a foundation for building modern web applications. Whether you're creating REST APIs. With
built-in support for
routing, request handling, and response generation, you can focus on implementing your application's logic without the
hassle of low-level details.

___
# Requirements


- **Java**: Version 17 or later.
- **Maven**: Make sure Maven is installed on your system. You can download it
  from [here](https://maven.apache.org/download.cgi).

___
# Getting Started

Follow these steps to get started with our project:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Svydovets-Bobocode-Java-Ultimate-3-0/Bring.git
   cd Bring
    ```

2. **Build the Project:**
   ```bash
    mvn clean install
    ```

3. **To test Bring Application, create new Maven Project and add dependency:**
   ```xml
    <dependency>
      <groupId>com.bobocode.ultimate</groupId>
      <artifactId>bring-svydovets</artifactId>
      <version>1.0</version>
    </dependency>
    ```
4. **You can download the Bring demo project to simplify setup:** [DEMO Project](https://github.com/Svydovets-Bobocode-Java-Ultimate-3-0/Bring-Demo)
5. **Run the application:**
    ```java
      public class DemoApp {
      
        public static void main(String[] args) {
          var context = BringApplication.run(DemoApp.class);
        }
      }
    ```

6. **Enjoy :)**

# Svydovets features

___

### Core functionality

- **[Application context](#application-context)**
- **[@Configuration](#configuration)**
- **[@Bean](#bean)**
- **[@Component](#component)**
- **[@Scope](#scope)**
- **[@Qualifier](#qualifier)**
- **[@Primary](#primary)**
- **[@PostConstruct](#postConstruct)**
- **[@Autowired](#autowired)**
- **[@ComponentScan](#componentScan)**

### Web functionality

- **[@RestController](#restController)**
- **[@RequestMapping](#requestMapping)**
- **[@RequestParam](#requestParam)**
- **[@PathVariable](#pathVariable)**
- **[@GetMapping](#getMapping)**
- **[@PostMapping](#postMapping)**
- **[@PutMapping](#putMapping)**
- **[@DeleteMapping](#deleteMapping)**
- **[@PatchMapping](#patchMapping)**

___

#### Application context

ApplicationContext - main part of the IoC container. ApplicationContext - a magic box that helps application run smoothly.
When application starts, it tells the ApplicationContext about all different beans it has. Each bean has responsibilities, like being saving data.
The ApplicationContext keeps track of all these beans and makes sure they are ready to do their job when it needed.

<details>
<summary>Example of code</summary> 

```java
public class Application {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.example.bring_test.beans");
        Map<String, Object> beans = applicationContext.getBeans();
    }
}
```

</details>

___
#### @Configuration

The @Configuration annotation is used to indicate that a class declares one or more @Bean methods, and define user's custom beans.
The @Configuration annotation is an integral part of the creating application context.

<details>
<summary>Example of code</summary> 

```java

@Configuration
@ComponentScan("com.example.bring_test.ioc.beans")
public class TestConfig {
}
```

</details>

___
#### @Bean

Bean is object that is instantiated, assembled, and managed by the IoC container
The Bean annotation is a custom annotation that you can use in your own implementation of the Bring framework.
It is used to mark methods in code that produce bean that should be managed.

<details>
<summary>Example of code</summary> 

```java

@Configuration
@ComponentScan("com.example.bring_test.ioc.beans")
public class TestConfig {

    @Bean
    public MessageService messageService() {
        MessageService messageService = new MessageService();
        messageService.setMessage("Hello from \"MessageService\"");
        return messageService;
    }

    @Bean
    public PrintService printService(MessageService messageService) {
        return new PrintService(messageService);
    }
}
```

</details>

___
#### @Component

The @Component annotation is used to indicate that a class is component of context. 
It is a generic annotation that designates a class as bean, allowing IoC container to manage its lifecycle.
Components are typically Java classes that encapsulate business logic or other processing capabilities within a user application.

<details>
<summary>Example of code</summary> 

```java

@Component
public class MessageService {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

</details>

___
#### @Scope

The **@Scope** annotation in Java is used to specify the lifecycle of a bean within the application context.
It enables developers to define whether a bean should be a singleton, with a single shared instance across the entire
application, or a prototype, with a new instance created for each request or reference.

**Singleton scope**
<details>
<summary>Example of code with the Singleton scope</summary> 

```java
@Component
@Scope(ApplicationContext.SCOPE_SINGLETON)
public class SingletonCandidate {

}
```
</details>

**Prototype scope**
<details>
<summary>Example of code with the Prototype scope</summary> 

```java
@Component
@Scope(ApplicationContext.SCOPE_PROTOTYPE)
public class PrototypeCandidate {
}
```
</details>

___
#### @Qualifier

The **@Qualifier** annotation in Java is utilized to enhance bean injection by providing a means to disambiguate and
specify a particular bean when there are multiple candidates of the same type within the application context.

<details>
<summary>Example of code</summary> 

```java

@Component
public class OrderService {

    @Autowired
    @Qualifier("storeItem")
    private Item item;

    public Item getItem() {
        return item;
    }
}
```

</details>

___
#### @Primary

@Primary is an effective way to use autowiring by type with several instances when one primary candidate can be determined.
When marked with @Primary, a bean is designated as the primary candidate for autowiring within a specific type, meaning it takes precedence over other candidates of the same type.

<details>
<summary>Example of code</summary> 

```java

@Primary
@Component
public class PrimaryInjectionCandidate implements InjectionCandidate {
}
```

</details>

___
#### @PostConstruct

The **@PostConstruct** annotation in Java as a marker for methods that should be executed
immediately after an object has been instantiated and its dependencies have been injected or initialized.
This annotation allows developers to define post-construction logic for objects without relying on any specific
framework or container.

<details>
<summary>Example of code</summary> 

```java

@Component
public class ServiceWithPostConstruct {

    private String message;

    @PostConstruct
    public void init() {
        message = "Message loaded with @PostConstruct";
    }

    public String getMessage() {
        return message;
    }
}
```

</details>

___
#### @Autowired

The **@Autowired** annotation in Java is employed to facilitate automatic dependency injection, allowing developers
to inject dependencies into beans without explicit instantiation. This annotation can be applied to fields, methods,
or constructors to signal the container to automatically resolve and provide the necessary dependencies.

**Example of the constructor autowired**

<details>
<summary>Example of code with the constructor inject</summary>

```java

@Component
public class ServiceWithAutowiredConstructor {

    private final MessageService messageService;

    @Autowired
    public ServiceWithAutowiredConstructor(MessageService messageService) {
        this.messageService = messageService;
    }
}
```

</details>

**Example of the method autowired**

<details>
<summary>Example of code with the method inject</summary>

```java

@Component
public class TrimService {
    private CommonService commonService;

    @Autowired
    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    public CommonService getCommonService() {
        return commonService;
    }
}

```

</details>

**Example of the field autowired**

<details>
<summary>Example of code with the field inject</summary>

````java

@Component
public class EditService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private InjectionCandidate injectionCandidate;
}

````

</details>

___
#### @ComponentScan

The custom **@CustomComponentScan** annotation in Java serves as a marker for configuration classes that enable
the automatic detection and registration of custom components within specified base packages. This annotation provides
a mechanism for developers to implement component scanning functionality without relying on any specific framework.

<details>
<summary>Example of code</summary> 

```java

@Configuration
@ComponentScan("com.exmaple.bring_test.beans")
public class ScanConfigTest {
}
```

</details>

___
#### @RestController

The **@RestController** annotation handles HTTP requests and generate responses. This annotation designates a class as a controller specifically
focused on building REST APIs, where methods within the class are responsible for processing requests and producing
responses in a JSON format.

<details>
<summary>Example of code</summary> 

```java

@RestController
public class UserController {
}
```

</details>

___
#### @RequestMapping

The **@RequestMapping** annotation is used to establish the base URL for the class 
that was marked with the **@RestController** annotation.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
}
```

</details>

___
#### @RequestParam

The **@RequestParam** annotation is used to extract and bind query parameters from the URL of an HTTP request
to the parameters of a method. This annotation simplifies the process of handling request parameters
and allows developers to easily access and use them within their methods.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
    @GetMapping
    public User getOneByFirstName(@RequestParam String firstName) {
        return userMap.values()
                .stream()
                .filter(user -> user.getFirstName().equals(firstName))
                .findAny()
                .orElseThrow();
    }
}
```

</details>

___
#### @PathVariable

The **@PathVariable** annotation is utilized to extract values from the URI path of an HTTP request
and bind them to the parameters of a method. This annotation simplifies the process of
handling path variables, allowing developers to access and utilize them within their methods.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {

    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public User getOneById(@PathVariable Long id) {
        return userMap.get(id);
    }
}

```

</details>

___
#### @GetMapping

The **@GetMapping** annotation is designed for handling HTTP GET requests.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @GetMapping
    public User getOneByFirstName(@RequestParam String firstName) {
        return userMap.values()
                .stream()
                .filter(user -> user.getFirstName().equals(firstName))
                .findAny()
                .orElseThrow();
    }

    @GetMapping("/{id}")
    public User getOneById(@PathVariable Long id) {
        return userMap.get(id);
    }
}
```

</details>

___
#### @PostMapping

The **@PostMapping** annotation is designed for handling HTTP POST requests.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @PostMapping
    public Long save(@RequestBody User user) {
        Long generatedId = idGenerator.incrementAndGet();
        user.setId(generatedId);
        userMap.put(generatedId, user);

        return generatedId;
    }
}
```

</details>

___
#### @PutMapping

The **@PutMapping** annotation is designed for handling HTTP PUT requests.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) {
        User savedUser = userMap.get(id);
        savedUser.setFirstName(user.getFirstName());
        savedUser.setLastName(user.getLastName());
        savedUser.setStatus(user.getStatus());
        userMap.put(id, savedUser);

        return savedUser;
    }
}
```

</details>

___
#### @DeleteMapping

The **@DeleteMapping** annotation is designed for handling HTTP DELETE requests.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {

    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @DeleteMapping("/{id}")
    public void removeWithServletRequestAndResponse(@PathVariable Long id) {
        userMap.remove(id);
    }
}
```

</details>

___
#### @PatchMapping

The **@PatchMapping** annotation is designed for handling HTTP PATCH requests.

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {

    private AtomicLong idGenerator = new AtomicLong(0L);
    private Map<Long, User> userMap = new ConcurrentHashMap<>();

    @PatchMapping
    public User updateUserFirstNameById(@PathVariable Long id, @RequestBody String firstName) {
        User user = userMap.get(id);
        user.setFirstName(firstName);
        userMap.put(id, user);

        return user;
    }
}
```

</details>