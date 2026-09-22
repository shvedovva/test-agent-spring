# Чек-лист проверки работы Todo List приложения

## 1. PostgreSQL запущен?

```bash
# Windows
net start postgresql

# Linux
sudo systemctl status postgresql

# macOS
brew services list | grep postgresql
```

## 2. База данных существует?

```bash
psql -U postgres -c "\l" | grep todolist
```

Если нет, создайте:
```bash
psql -U postgres
CREATE DATABASE todolist OWNER myuser;
\q
```

## 3. Пользователь имеет доступ?

```bash
psql -U myuser -d todolist -c "SELECT 1;"
```

Если ошибка, создайте пользователя:
```bash
psql -U postgres
CREATE USER myuser WITH PASSWORD '12345678';
GRANT ALL PRIVILEGES ON DATABASE todolist TO myuser;
\q
```

## 4. Таблицы созданы?

```bash
psql -U myuser -d todolist -c "\dt"
```

Если нет, создайте:
```bash
psql -U myuser -d todolist
CREATE TABLE IF NOT EXISTS todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
\q
```

## 5. Приложение запущено?

```bash
curl http://localhost:8080/app/test/db-status
```

Ожидаемый ответ:
```
Database connection test: OK
Service bean: com.example.todolist.service.TodoService
```

## 6. Добавить задачу?

```bash
curl -X POST http://localhost:8080/app/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Тест","description":"Тест","status":"PENDING"}'
```

## 7. Проверить в БД?

```bash
psql -U myuser -d todolist -c "SELECT * FROM todos;"
```

## 8. Проверить через браузер?

- http://localhost:8080/app/api/todos (GET)
- http://localhost:8080/app/test/db-status (GET)
- http://localhost:8080/app/test/service-test (GET)

## 9. Логи приложения?

```bash
# Tomcat logs
tail -f /path/to/tomcat/logs/catalina.out

# PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-*.log
```

## 10. Если данные не сохраняются?

1. Проверьте логи приложения на ошибки
2. Проверьте, что PostgreSQL работает
3. Проверьте, что таблицы созданы
4. Проверьте права пользователя
5. Проверьте, что `@Transactional` работает

## Быстрый старт

```bash
# 1. Запустите PostgreSQL
sudo systemctl start postgresql

# 2. Создайте базу данных
psql -U postgres -c "CREATE DATABASE todolist OWNER myuser;"

# 3. Запустите приложение
mvn spring-boot:run

# 4. Проверьте
curl http://localhost:8080/app/test/db-status

# 5. Добавьте задачу
curl -X POST http://localhost:8080/app/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Тест","description":"Тест","status":"PENDING"}'

# 6. Проверьте в БД
psql -U myuser -d todolist -c "SELECT * FROM todos;"
```
