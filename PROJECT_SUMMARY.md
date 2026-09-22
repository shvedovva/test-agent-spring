# 🎉 Spring Todo List API — Полный Проект без Spring Boot

## 📦 Что было создано

Полноценное Spring-приложение **без Spring Boot** с:
- ✅ REST API для задач (TodoList)
- ✅ JPA/Hibernate для работы с БД
- ✅ MySQL база данных
- ✅ Frontend (HTML/CSS/JS)
- ✅ XML конфигурация
- ✅ WAR-файл для Tomcat

---

## 📂 Файлы проекта

```
src/
├── main/
│   ├── java/
│   │   └── com/example/todolist/
│   │       ├── TodoListApplication.java      # Точка входа
│   │       ├── SpringRunner.java             # Запуск приложения
│   │       ├── config/
│   │       │   └── AppConfig.java            # Spring конфигурация
│   │       ├── controller/
│   │       │   └── TodoController.java       # REST контроллеры
│   │       ├── service/
│   │       │   └── TodoService.java          # Бизнес-логика
│   │       ├── repository/
│   │       │   └── TodoRepository.java       # DAO слой
│   │       └── model/
│   │           ├── Todo.java                 # Сущность
│   │           └── TodoStatus.java           # Enum
│   ├── resources/
│   │   ├── application-context.xml           # XML конфигурация
│   │   └── META-INF/
│   │       └── context.xml                   # Tomcat контекст
│   └── webapp/
│       ├── WEB-INF/
│       │   └── web.xml                       # Servlet конфигурация
│       ├── index.html                        # HTML страница
│       ├── css/
│       │   └── style.css                     # Стили
│       └── js/
│           └── app.js                        # JavaScript
```

---

## 🎯 Ключевые моменты

### 1. Без Spring Boot!

```xml
<!-- В pom.xml -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
</dependency>
<!-- НЕТ spring-boot-starter! -->
```

### 2. XML Конфигурация

```xml
<!-- application-context.xml -->
<beans>
    <context:component-scan base-package="com.example.todolist"/>
    <tx:annotation-driven/>
    <bean id="dataSource" .../>
    <bean id="entityManagerFactory" .../>
</beans>
```

### 3. Dependency Injection

```java
// TodoService.java
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
}
```

Spring сам создаст и вставит TodoRepository!

### 4. REST Контроллеры

```java
// TodoController.java
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @Autowired
    private TodoService todoService;
    
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }
}
```

### 5. JPA Repository

```java
// TodoRepository.java
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    // Методы генерируются автоматически!
}
```

### 6. Запуск приложения

```java
// SpringRunner.java
public class SpringRunner {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = 
            new ClassPathXmlApplicationContext("application-context.xml");
        context.start();
        System.out.println("✓ Spring запущен!");
    }
}
```

---

## 🚀 Как запустить

### Способ 1: Через Maven (рекомендуется)

```bash
# Установите Maven
sudo apt install maven

# Соберите проект
mvn clean package

# Запустите
mvn spring-boot:run
```

### Способ 2: Через Tomcat

```bash
# Соберите WAR-файл
jar -cf target/todo-list.war \
    -C src/main/java/ . \
    -C src/main/resources/ . \
    -C src/main/webapp/ .

# Запустите Tomcat
cd tomcat-10.1.30
cp target/todo-list.war webapps/
bin/startup.sh

# Откройте в браузере
http://localhost:8080/todo-list/
```

### Способ 3: Через Java

```bash
# Запустите SpringRunner
java -cp "target/todo-list.war:..." com.example.todolist.TodoListApplication
```

---

## 📖 Документация

- **README.md** — Полная документация проекта
- **RUN.md** — Инструкция по установке и запуску
- **EXPLANATION.md** — Подробное объяснение каждого шага
- **PROJECT_SUMMARY.md** — Этот файл

---

## 🎓 Что вы узнали

### 1. Spring ApplicationContext

```java
ClassPathXmlApplicationContext context = 
    new ClassPathXmlApplicationContext("application-context.xml");
```

Создаёт и запускает Spring Container.

### 2. Dependency Injection

```java
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
}
```

Spring сам создаёт объекты и вставляет их.

### 3. Inversion of Control (IoC)

Вы не контролируете создание объектов — Spring контролирует.

### 4. JPA/Hibernate

```java
@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

Маппит Java объекты в SQL таблицы.

### 5. REST API

```java
@RestController
@RequestMapping("/api/todos")
public class TodoController {
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() { ... }
}
```

Создаёт REST endpoints.

### 6. XML Конфигурация

```xml
<beans>
    <context:component-scan base-package="..."/>
    <bean id="dataSource" .../>
</beans>
```

Настраивает Spring через XML.

---

## 🧪 Тестирование API

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

## 📊 Сравнение: Spring vs Spring Boot

| Spring | Spring Boot |
|--------|-------------|
| XML конфигурация | Java Config |
| Явный ApplicationContext | Автомагический |
| Tomcat как внешняя зависимость | Встроенный Tomcat |
| Явное подключение к БД | Auto-configuration |
| WAR-файл | JAR-файл |
| Множество файлов config | Один файл |
| **Понимание внутреннего устройства** | **Быстрая разработка** |

---

## 🎬 Демо

Откройте `http://localhost:8080/todo-list/` в браузере и:

1. Создайте задачу
2. Получите список задач
3. Обновите статус задачи
4. Удалите задачу

---

## 🏆 Итог

Этот проект показывает:
1. ✅ Как работает Spring без Boot
2. ✅ Как настраивается ApplicationContext
3. ✅ Как работает Dependency Injection
4. ✅ Как работает JPA/Hibernate
5. ✅ Как создаются REST контроллеры
6. ✅ Как работает Tomcat с Spring

**Это фундаментальное понимание Spring Framework!**

---

## 📞 Поддержка

Если что-то не работает:
1. Проверьте, что MySQL запущен
2. Проверьте пароль в AppConfig.java
3. Проверьте, что WAR-файл собран правильно
4. Посмотрите логи Tomcat

---

**Создано для обучения Spring без Spring Boot! 🚀**
