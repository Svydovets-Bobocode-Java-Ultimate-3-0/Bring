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
        private static final Logger log = LoggerFactory.getLogger(DemoApp.class);
      
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
```

</details>

#### @Configuration

___

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @Bean

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @Component

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @Qualifier

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @Primary

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @PostConstruct

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @Autowired

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @ComponentScan

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @RestController

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @RequestMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @RequestParam

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @PathVariable

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @GetMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @PostMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @PutMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @DeleteMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>

#### @PatchMapping

___

Some description

<details>
<summary>Example of code</summary> 

```java
```

</details>