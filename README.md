# Bring Svydovets

In the dynamic landscape of web development, the combination of a robust Web module and an efficient Dependency
Injection system is crucial for crafting maintainable and extensible software. Bring Svydovets brings together the best
of
both worlds, offering a comprehensive framework that simplifies web application development while promoting loose
coupling and code organization through Dependency Injection.

# Key Features

___

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

# Requirements

___

- **Java**: Version 17 or later.
- **Maven**: Make sure Maven is installed on your system. You can download it
  from [here](https://maven.apache.org/download.cgi).

# Getting Started

___
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

3. **Add the Project as a dependency:**
   ```xml
    <dependency>
      <groupId>org.example</groupId>
      <artifactId>Bring-demo</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
    ```
4. **Run the application:**
    ```java
      public class DemoApp {
      
        public static void main(String[] args) {
          var context = BringApplication.run(DemoApp.class);
        }
      }
    ```

5. **Enjoy :)**

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

___

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

#### @Configuration

___

<details>
<summary>Example of code</summary> 

```java

@Configuration
@ComponentScan("com.example.bring_test.ioc.beans")
public class TestConfig {
}
```

</details>

#### @Bean

___

Some description

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

#### @Component

___

Some description

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

#### @Scope

___

Some description

<details>
<summary>Example of code</summary> 

```java

@Component
@Scope(ApplicationContext.SCOPE_SINGLETON)
public class SingletonCandidate {

}

@Component
@Scope(ApplicationContext.SCOPE_PROTOTYPE)
public class PrototypeCandidate {

}
```

</details>


#### @Qualifier

___

Some description

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

#### @Primary

___

Some description

<details>
<summary>Example of code</summary> 

```java

@Primary
@Component
public class PrimaryInjectionCandidate implements InjectionCandidate {
}
```

</details>

#### @PostConstruct

___

Some description

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

#### @Autowired

___

Some description

<details>
<summary>Example of code</summary> 

```java

@Component
public class ServiceWithAutowiredConstructor {

    private final MessageService messageService;

    @Autowired
    public ServiceWithAutowiredConstructor(MessageService messageService) {
        this.messageService = messageService;
    }
}

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

@Component
public class EditService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private InjectionCandidate injectionCandidate;
}
```

</details>

#### @ComponentScan

___

Some description

<details>
<summary>Example of code</summary> 

```java

@Configuration
@ComponentScan("com.exmaple.bring_test.beans")
public class ScanConfigTest {
}
```

</details>

#### @RestController

___

Some description

<details>
<summary>Example of code</summary> 

```java

@RestController
public class UserController {
}
```

</details>

#### @RequestMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java

@RestController
@RequestMapping("/users")
public class UserController {
}
```

</details>

#### @RequestParam

___

Some description

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

#### @PathVariable

___

Some description

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

#### @GetMapping

___

Some description

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

#### @PostMapping

___

Some description

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

#### @PutMapping

___

Some description

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

#### @DeleteMapping

___

Some description

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

#### @PatchMapping

___

Some description

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