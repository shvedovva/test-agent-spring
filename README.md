# Todo List API — Spring без Spring Boot

Полноценное Spring-приложение **без Spring Boot**. Это учебный проект, показывающий, как работает Spring "под капотом".

## 📋 Что вы узнаете

- Как работает **Spring ApplicationContext** без Boot
- Как настраивается **JPA/Hibernate** вручную
- Как работает **Dependency Injection**
- Как создаются **REST контроллеры**
- Как работает **Tomcat** с WAR-файлом

## 🏗️ Архитектура

```
com.example.todolist
├── config/          # Конфигурация Spring
├── controller/      # REST контроллеры
├── service/         # Бизнес-логика
├── repository/      # DAO слой (JPA)
├── model/           # Сущности (JPA Entity)
└── TodoListApplication.java  # Точка входа
```

---

## 🚀 Установка и запуск

### 1. Подготовьте MySQL

```sql
CREATE DATABASE todolist;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
```

### 2. Запустите MySQL

```bash
# Linux
sudo systemctl start mysql

# Windows
net start MySQL
```

### 3. Соберите проект

```bash
mvn clean package
```

### 4. Запустите приложение

```bash
java -cp target/todo-list-1.0-SNAPSHOT.war \
     target/classes \
     target/*/*.jar \
     com.example.todolist.TodoListApplication
```

ИЛИ используйте Maven:

```bash
mvn spring-boot:run
```

---

## 📖 Объяснение каждого шага

### Шаг 1: **pom.xml** — Maven конфигурация

**Что делаем:** Настраиваем зависимости Spring Framework.

**Важно:** Мы НЕ используем `spring-boot-starter`. Вместо этого:
- `spring-context` — база Spring
- `spring-web` — для REST контроллеров
- `spring-jdbc` — для работы с БД
- `hibernate-core` — ORM
- `mysql-connector-java` — драйвер БД

**Почему WAR:** Spring Boot использует JAR, но классический Spring — WAR для deployment в Tomcat.

---

### Шаг 2: **Модель данных** — Todo.java

**Что делаем:** Определяем сущность с аннотациями JPA.

**Ключевые аннотации:**
- `@Entity` — это JPA сущность
- `@Table` — имя таблицы в БД
- `@Id` — первичный ключ
- `@GeneratedValue` — автогенерация ID
- `@Column` — маппинг поля в столбец
- `@Enumerated` — маппинг enum в БД

**Почему нужен конструктор без аргументов:** JPA требует его для создания объектов.

---

### Шаг 3: **Enum статусов**

**Что делаем:** Перечисляем возможные состояния задач.

**Аннотация `@Enumerated(EnumType.STRING)`:**
Сохраняет enum как строку в БД (PENDING, IN_PROGRESS, COMPLETED).

---

### Шаг 4: **Repository** — TodoRepository.java

**Что делаем:** Определяем интерфейс для работы с БД.

**JpaRepository** автоматически предоставляет:
- `findAll()` — получить все
- `findById()` — получить по ID
- `save()` — создать или обновить
- `deleteById()` — удалить

**@Repository:** Маркер аннотация, Spring знает, что это DAO.

---

### Шаг 5: **Service** — TodoService.java

**Что делаем:** Реализуем сервис с бизнес-логикой.

**Dependency Injection:**
```java
private final TodoRepository todoRepository;

public TodoService(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
}
```

Spring сам создаст экземпляр TodoRepository и вставит его в конструктор.

**@Service:** Маркер аннотация для сервисов.

**@Transactional:** Управление транзакциями.

---

### Шаг 6: **REST Контроллер** — TodoController.java

**Что делаем:** Создаём REST API endpoints.

**Ключевые аннотации:**
- `@RestController` — контроллер, который возвращает JSON
- `@RequestMapping("/api/todos")` — базовый URL
- `@GetMapping` — GET запрос
- `@PostMapping` — POST запрос
- `@PutMapping` — PUT запрос
- `@DeleteMapping` — DELETE запрос
- `@PathVariable` — параметр из URL

**@Autowired:** Инъекция Service в контроллер.

---

### Шаг 7: **AppConfig** — Spring конфигурация

**Что делаем:** Настраиваем Spring Container.

**Самое важное в Spring без Boot!**

**@Configuration:** Маркер аннотация для конфигурации.

**@EnableTransactionManagement:** Включаем транзакции.

**@ComponentScan:** Сканируем пакеты на компоненты (@Component, @Service, @Repository, @Controller).

**Bean методы:**
```java
@Bean
public DataSource dataSource() { ... }
```

Spring создаст объект и зарегистрирует его в контейнере.

**LocalContainerEntityManagerFactoryBean:** Настройка JPA/Hibernate.

**HibernateJpaVendorAdapter:** Выбор платформы БД (MySQL8).

---

### Шаг 8: **application-context.xml** — XML конфигурация

**Что делаем:** Создаём файл конфигурации Spring.

**Почему XML?** В Spring Boot всё делается через Java Config, но классический Spring использует XML.

**Основные секции:**
- `<context:component-scan>` — сканирование компонентов
- `<tx:annotation-driven>` — транзакции
- `<bean>` — определение бина

**Важно:** Этот файл запускает весь Spring ApplicationContext.

---

### Шаг 9: **TodoListApplication.java** — точка входа

**Что делаем:** Создаём класс для инициализации Spring.

**AbstractDispatcherServletInitializer:**
Это класс Spring MVC, который автоматически встраивается в web.xml.

**Методы:**
- `createWebApplicationContext()` — создаёт контекст
- `getConfigLocation()` — путь к конфигурации
- `customizeWebApplicationContext()` — кастомизация

**Почему не @SpringBootApplication?**
Это аннотация Spring Boot, которая делает всё автоматически. Здесь мы делаем вручную.

---

### Шаг 10: **web.xml** — конфигурация Servlet

**Что делаем:** Настраиваем DispatcherServlet.

**Важные секции:**
- `<listener>` — ContextLoaderListener инициализирует Spring
- `<context-param>` — путь к контекстной конфигурации
- `<servlet>` — определение DispatcherServlet
- `<servlet-mapping>` — URL паттерн `/api/*`

**Почему web.xml?** Это стандарт Java EE для настройки servlets.

---

### Шаг 11-12: **Папки для ресурсов**

**Что делаем:** Создаём папки для CSS, JS, и статики.

Структура:
```
src/main/webapp/
├── WEB-INF/
│   └── web.xml
├── css/
│   └── style.css
├── js/
│   └── app.js
└── index.html
```

---

### Шаг 13: **index.html** — фронтенд

**Что делаем:** Создаём HTML-страницу.

**API calls:**
```javascript
fetch('/api/todos') // GET все задачи
fetch('/api/todos/status/PENDING') // GET по статусу
fetch('/api/todos', { method: 'POST', body: JSON.stringify(...) }) // POST
```

---

### Шаг 14: **CSS стили**

**Что делаем:** Добавляем базовые стили.

---

### Шаг 15: **JavaScript**

**Что делаем:** Клиентский код для работы с API.

**fetch API:** Современный способ HTTP запросов.

---

### Шаг 16: **SpringRunner.java** — запуск

**Что делаем:** Класс для запуска приложения.

**ClassPathXmlApplicationContext:**
Загружает конфигурацию из XML файла.

**context.start():** Запускает Tomcat сервер.

**context.close():** Останавливает приложение.

---

## 🎯 Как это работает

### 1. Запуск приложения

```bash
java -cp target/todo-list-1.0-SNAPSHOT.war ... com.example.todolist.TodoListApplication
```

### 2. Инициализация Spring

1. `ClassPathXmlApplicationContext` загружает `application-context.xml`
2. Spring сканирует пакеты на компоненты
3. Создаёт все бобы (DataSource, EntityManagerFactory, TransactionManager)
4. Регистрирует сервисы и контроллеры

### 3. Запрос к API

```bash
curl http://localhost:8080/api/todos
```

1. Tomcat получает запрос
2. DispatcherServlet перехватывает его
3. Controller обрабатывает запрос
4. Service выполняет бизнес-логику
5. Repository обращается к БД
6. JSON возвращается клиенту

### 4. Работа с БД

1. Hibernate создаёт таблицы автоматически (`generateDdl=true`)
2. JPA маппит сущности в таблицы
3. Repository предоставляет CRUD операции

---

## 🔧 Тестирование API

```bash
# Создать задачу
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин"}'

# Получить все задачи
curl http://localhost:8080/api/todos

# Получить задачу по ID
curl http://localhost:8080/api/todos/1

# Обновить задачу
curl -X PUT http://localhost:8080/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин", "status": "COMPLETED"}'

# Удалить задачу
curl -X DELETE http://localhost:8080/api/todos/1

# Фильтр по статусу
curl http://localhost:8080/api/todos/status/PENDING
```

---

## 📚 Ключевые отличия от Spring Boot

| Spring | Spring Boot |
|--------|-------------|
| XML конфигурация | Java Config |
| Явный ApplicationContext | Автомагический |
| Tomcat как внешняя зависимость | Встроенный Tomcat |
| Явное подключение к БД | Auto-configuration |
| WAR-файл | JAR-файл |
| Множество файлов config | Один файл |

---

## 🎓 Что вы узнали

1. **Spring ApplicationContext** — как он создаётся
2. **Dependency Injection** — как работает
3. **JPA/Hibernate** — как настраивается
4. **REST контроллеры** — как создаются
5. **Tomcat + Spring** — как работают вместе
6. **WAR-деплоемет** — как deploy в Tomcat
7. **XML конфигурация** — как настраивается классический Spring

---

## 🚀 Следующие шаги

- Добавить тесты (@SpringBootTest)
- Добавить безопасность (@PreAuthorize)
- Добавить кэширование (@Cacheable)
- Добавить асинхронность (@Async)
- Добавить сообщения (Spring MVC ViewResolver)
