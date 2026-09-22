# 🎓 Объяснение проекта Spring без Spring Boot

Каждый файл и каждая строка кода написаны с объяснениями. Давайте разберём по шагам, как это работает.

---

## 📁 Структура проекта

```
temp/test-agent-spring/
├── pom.xml                          # Maven конфигурация
├── README.md                        # Документация
├── RUN.md                           # Инструкция по запуску
├── EXPLANATION.md                   # Этот файл
├── build-manual.sh                  # Скрипт сборки
├── build.sh                         # Скрипт сборки (упрощённый)
├── run.sh                           # Скрипт запуска
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/todolist/
│   │   │       ├── TodoListApplication.java    # Точка входа
│   │   │       ├── SpringRunner.java           # Запуск приложения
│   │   │       ├── config/
│   │   │       │   └── AppConfig.java          # Spring конфигурация
│   │   │       ├── controller/
│   │   │       │   └── TodoController.java     # REST контроллеры
│   │   │       ├── service/
│   │   │       │   └── TodoService.java        # Бизнес-логика
│   │   │       ├── repository/
│   │   │       │   └── TodoRepository.java     # DAO слой
│   │   │       ├── model/
│   │   │       │   ├── Todo.java              # Сущность
│   │   │       │   └── TodoStatus.java        # Enum статусов
│   │   │       └── TodoListApplication.java   # Точка входа (дубликат)
│   │   ├── resources/
│   │   │   ├── application-context.xml        # XML конфигурация Spring
│   │   │   └── META-INF/
│   │   │       └── context.xml                # Tomcat контекст
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── web.xml                    # Servlet конфигурация
│   │       ├── index.html                     # HTML страница
│   │       ├── css/
│   │       │   └── style.css                  # Стили
│   │       └── js/
│   │           └── app.js                     # JavaScript клиент
└── target/                                    # Собранный WAR-файл
    └── todo-list.war
```

---

## 🔍 Подробное объяснение каждого компонента

### 1. pom.xml — Maven Configuration

**Цель:** Настроить зависимости и сборку проекта.

**Ключевые моменты:**

```xml
<packaging>war</packaging>
```
- WAR-архив для деплоя в Tomcat
- Spring Boot использует JAR

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>
```
- **spring-context**: Ядро Spring, управление бинами
- **spring-web**: REST контроллеры, HTTP
- **spring-jdbc**: Работа с базой данных
- **hibernate-core**: ORM (объектно-реляционное маппинг)

**Важно:** Нет `spring-boot-starter`! Это главное отличие.

---

### 2. Todo.java — Entity Model

**Цель:** Определить сущность, которая будет храниться в БД.

```java
@Entity
@Table(name = "todos")
public class Todo {
```

- `@Entity`: Это JPA сущность (эквивалент таблицы в БД)
- `@Table`: Имя таблицы в БД — "todos"

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

- `@Id`: Первичный ключ
- `@GeneratedValue`: Автоинкремент ID (как в MySQL)

```java
@Column(nullable = false, length = 200)
private String title;
```

- `@Column`: Маппинг поля в столбец таблицы
- `nullable = false`: Обязательное поле
- `length = 200`: Максимальная длина

```java
@Enumerated(EnumType.STRING)
private TodoStatus status;
```

- `@Enumerated`: Преобразует enum в строку в БД
- `EnumType.STRING`: Хранит как "PENDING", "COMPLETED"

**Почему нужен конструктор без аргументов?**

```java
public Todo() {
    this.createdAt = LocalDateTime.now();
    this.status = TodoStatus.PENDING;
}
```

JPA требует конструктор без аргументов для создания объектов. Это важно!

---

### 3. TodoStatus.java — Enum

**Цель:** Определить возможные состояния задачи.

```java
public enum TodoStatus {
    PENDING,    // Не выполнена
    IN_PROGRESS, // В процессе
    COMPLETED   // Выполнена
}
```

Этот enum будет преобразован в строки: "PENDING", "IN_PROGRESS", "COMPLETED".

---

### 4. TodoRepository.java — DAO Layer

**Цель:** Определять интерфейс для работы с БД.

```java
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
```

- `@Repository`: Spring знает, что это DAO
- `JpaRepository`: JPA предоставляет методы автоматически

**Автоматические методы:**

```java
List<Todo> findAll();
Todo findById(Long id);
Todo save(Todo todo);
void deleteById(Long id);
```

Spring автоматически реализует эти методы через прокси. Это называется **Spring Data JPA**.

**Почему это важно?** Не нужно писать SQL вручную! Spring генерирует запросы.

---

### 5. TodoService.java — Business Logic

**Цель:** Реализовать бизнес-логику.

```java
@Service
@Transactional
public class TodoService {
```

- `@Service`: Маркер аннотация для сервисов
- `@Transactional`: Все методы в транзакции

**Dependency Injection:**

```java
private final TodoRepository todoRepository;

public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
}
```

**Как это работает:**

1. Spring создаёт экземпляр TodoRepository
2. Spring вставляет его в конструктор TodoService
3. Вы не создаёте объект вручную — Spring делает это!

**Это называется Dependency Injection (DI).**

**Почему это важно?** Вы не связаны с конкретным реализацией. Можно легко поменять базу данных.

---

### 6. TodoController.java — REST API

**Цель:** Создать REST endpoints для работы с API.

```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {
```

- `@RestController`: Контроллер, который возвращает JSON
- `@RequestMapping`: Базовый URL — "/api/todos"

**GET все задачи:**

```java
@GetMapping
public ResponseEntity<List<Todo>> getAllTodos() {
    List<Todo> todos = todoService.getAllTodos();
    return ResponseEntity.ok(todos);
}
```

- `@GetMapping`: Обрабатывает GET запросы
- `ResponseEntity.ok()`: Возвращает HTTP 200 + JSON

**POST создать задачу:**

```java
@PostMapping
public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
    Todo createdTodo = todoService.createTodo(todo);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
}
```

- `@PostMapping`: Обрабатывает POST запросы
- `@RequestBody`: JSON из тела запроса
- `HttpStatus.CREATED`: Возвращает HTTP 201

**PUT обновить задачу:**

```java
@PutMapping("/{id}")
public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
    Todo updated = todoService.updateTodo(id, updatedTodo);
    return ResponseEntity.ok(updated);
}
```

- `@PathVariable`: Параметр из URL (id)
- `@RequestBody`: JSON из тела запроса

**DELETE удалить задачу:**

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
    todoService.deleteTodo(id);
    return ResponseEntity.noContent().build();
}
```

- `@DeleteMapping`: Обрабатывает DELETE запросы
- `noContent()`: Возвращает HTTP 204

**Dependency Injection:**

```java
@Autowired
private TodoService todoService;
```

Spring вставляет сервис в контроллер.

---

### 7. AppConfig.java — Spring Configuration

**Цель:** Настроить Spring Container.

**Это самое важное в Spring без Boot!**

```java
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.example.todolist")
public class AppConfig {
```

- `@Configuration`: Это файл конфигурации
- `@EnableTransactionManagement`: Включаем транзакции
- `@ComponentScan`: Сканируем пакеты на компоненты

**DataSource:**

```java
@Bean
public DataSource dataSource() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds.setUrl("jdbc:mysql://localhost:3306/todolist?useSSL=false&serverTimezone=UTC");
    ds.setUsername("root");
    ds.setPassword("password");
    return ds;
}
```

- `@Bean`: Spring создаст этот объект и зарегистрирует в контейнере
- `DriverManagerDataSource`: Простой способ подключить к БД

**EntityManagerFactory:**

```java
@Bean
public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setDatabasePlatform(org.hibernate.dialect.MySQL8Dialect.class.getName());
    vendorAdapter.setGenerateDdl(true); // Автоматически создаёт таблицы
    
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource());
    emf.setPackagesToScan("com.example.todolist.model");
    emf.setJpaVendorAdapter(vendorAdapter);
    
    return emf;
}
```

- `LocalContainerEntityManagerFactoryBean`: Настройка JPA
- `packagesToScan`: Какие пакеты сущностей сканировать
- `generateDdl = true`: Автоматически создаёт таблицы

**TransactionManager:**

```java
@Bean
public JpaTransactionManager transactionManager() {
    JpaTransactionManager tm = new JpaTransactionManager();
    tm.setEntityManagerFactory(entityManagerFactory().getObject());
    return tm;
}
```

- Управление транзакциями для JPA

---

### 8. application-context.xml — XML Config

**Цель:** Загрузить Spring ApplicationContext.

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:jpa="http://www.springframework.org/schema/jpa"
       xsi:schemaLocation="...">
```

- `xmlns`: XML namespaces для Spring
- `xsi:schemaLocation`: Ссылка на XSD схемы

**Component Scan:**

```xml
<context:component-scan base-package="com.example.todolist"/>
```

- Сканирует пакеты на компоненты (@Component, @Service, @Repository, @Controller)

**Transaction Management:**

```xml
<tx:annotation-driven/>
```

- Включает аннотации транзакций (@Transactional)

**DataSource:**

```xml
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/todolist?useSSL=false&amp;serverTimezone=UTC"/>
    <property name="username" value="root"/>
    <property name="password" value="password"/>
</bean>
```

- `<bean>`: Определение бина
- `<property>`: Установка свойств

**EntityManagerFactory:**

```xml
<bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="packagesToScan" value="com.example.todolist.model"/>
    <property name="jpaVendorAdapter">
        <bean class="org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter">
            <property name="databasePlatform" value="org.hibernate.dialect.MySQL8Dialect"/>
            <property name="generateDdl" value="true"/>
        </bean>
    </property>
</bean>
```

- `ref="dataSource"`: Ссылка на другой bean
- `generateDdl = true`: Создаёт таблицы автоматически

---

### 9. TodoListApplication.java — Entry Point

**Цель:** Точка входа в приложение.

```java
public class TodoListApplication extends AbstractDispatcherServletInitializer {
```

- `AbstractDispatcherServletInitializer`: Spring MVC класс для инициализации

**createWebApplicationContext:**

```java
@Override
protected WebApplicationContext createWebApplicationContext() {
    return new org.springframework.web.context.support.XmlWebApplicationContext();
}
```

- Создаёт WebApplicationContext
- Загружает конфигурацию из XML

**getConfigLocation:**

```java
@Override
protected String getConfigLocation() {
    return "/application-context.xml";
}
```

- Путь к файлу конфигурации

---

### 10. web.xml — Servlet Config

**Цель:** Настроить Tomcat.

```xml
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

- `ContextLoaderListener`: Инициализирует Spring ApplicationContext

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>/WEB-INF/application-context.xml</param-value>
</context-param>
```

- Путь к конфигурации

```xml
<servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/application-context.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
```

- `DispatcherServlet`: Central point для всех запросов
- `load-on-startup=1`: Загружается первым

```xml
<servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/api/*</url-pattern>
</servlet-mapping>
```

- Все запросы `/api/*` идут к DispatcherServlet

---

### 11. SpringRunner.java — Запуск

**Цель:** Запустить Spring Container.

```java
public static void main(String[] args) {
    System.out.println("=== Запуск Spring TodoList Application ===");
    
    // Создаём и запускаем Spring Container
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
    
    // Получаем сущности Spring
    TodoService service = context.getBean(TodoService.class);
    TodoController controller = context.getBean(TodoController.class);
    
    System.out.println("✓ Spring Container успешно запущен!");
    
    // Запуск сервера Tomcat
    context.start();
    
    System.out.println("✓ Сервер доступен на: http://localhost:8080");
}
```

**ClassPathXmlApplicationContext:**

- Загружает конфигурацию из XML
- Создает все бобы (DataSource, Service, Controller)
- Запускает Tomcat

**context.getBean(TodoService.class):**

- Получает сервис из контейнера
- Spring сам создал его и проинжектил зависимости

---

## 🔄 Как работает всё вместе

### Запуск приложения

```
1. Java запускает SpringRunner.main()
2. ClassPathXmlApplicationContext загружает application-context.xml
3. Spring сканирует пакеты на компоненты (@Service, @Repository, @Controller)
4. Spring создаёт DataSource, EntityManagerFactory, TransactionManager
5. Spring создаёт все бобы (TodoService, TodoRepository, TodoController)
6. Spring вставляет зависимости через DI
7. Tomcat начинает слушать запросы
```

### Обработка запроса

```
1. Клиент отправляет GET /api/todos
2. Tomcat получает запрос
3. DispatcherServlet перехватывает запрос
4. TodoController.getAllTodos() вызывается
5. TodoService.getAllTodos() вызывается
6. TodoRepository.findAll() вызывается
7. Hibernate генерирует SQL: SELECT * FROM todos
8. MySQL возвращает данные
9. Hibernate маппит в Java объекты
10. TodoService возвращает List<Todo>
11. TodoController возвращает JSON
12. Клиент получает JSON
```

### Создание задачи

```
1. Клиент отправляет POST /api/todos с JSON
2. TodoController.createTodo() вызывается
3. TodoService.createTodo() вызывается
4. TodoRepository.save() вызывается
5. Hibernate генерирует SQL: INSERT INTO todos (title, description) VALUES (?, ?)
6. MySQL сохраняет задачу
7. Hibernate генерирует ID
8. MySQL возвращает ID
9. Hibernate маппит в Java объект
10. TodoService возвращает Todo
11. TodoController возвращает JSON с HTTP 201
12. Клиент получает JSON
```

---

## 🎯 Ключевые концепции

### 1. Dependency Injection (DI)

```java
// Без Spring Boot — вы делаете всё вручную
private final TodoRepository todoRepository;

public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
}
```

Spring сам создаёт объекты и вставляет их в конструкторы.

### 2. Inversion of Control (IoC)

Вы не контролируете создание объектов — Spring контролирует.

### 3. Aspect-Oriented Programming (AOP)

`@Transactional` работает через AOP — Spring оборачивает методы в транзакции.

### 4. Proxy Pattern

`JpaRepository` работает через прокси — Spring генерирует код для каждого метода.

### 5. ORM (Object-Relational Mapping)

Hibernate маппит Java объекты в SQL таблицы.

### 6. MVC (Model-View-Controller)

- **Model**: Todo.java — данные
- **View**: index.html — представление
- **Controller**: TodoController.java — обработка запросов

---

## 📚 Почему это важно?

### Spring Boot делает всё автоматически

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### Классический Spring требует ручную настройку

```java
// Настройка DataSource
@Bean DataSource dataSource() { ... }

// Настройка EntityManagerFactory
@Bean EntityManagerFactory emf() { ... }

// Настройка TransactionManager
@Bean TransactionManager tm() { ... }

// Настройка Context
ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
context.start();
```

**Понимание классического Spring помогает:**
- Понимать, что делает Spring Boot
- Настроить Spring без Boot
- Понимать внутреннюю работу фреймворка
- Решать проблемы, когда Boot не помогает

---

## 🔧 Тестирование API

```bash
# Создать задачу
curl -X POST http://localhost:8080/todo-list/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин"}'

# Получить все задачи
curl http://localhost:8080/todo-list/api/todos

# Получить задачу по ID
curl http://localhost:8080/todo-list/api/todos/1

# Обновить задачу
curl -X PUT http://localhost:8080/todo-list/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин", "status": "COMPLETED"}'

# Удалить задачу
curl -X DELETE http://localhost:8080/todo-list/api/todos/1

# Фильтр по статусу
curl http://localhost:8080/todo-list/api/todos/status/PENDING
```

---

## 📖 Заключение

Этот проект показывает:
1. Как работает Spring без Boot
2. Как настраивается ApplicationContext
3. Как работает Dependency Injection
4. Как работает JPA/Hibernate
5. Как создаются REST контроллеры
6. Как работает Tomcat с Spring

**Это фундаментальное понимание Spring Framework!**
