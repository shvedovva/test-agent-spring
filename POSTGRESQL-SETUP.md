# Настройка PostgreSQL для Todo List приложения

## Шаг 1: Установите PostgreSQL

### Windows
1. Скачайте PostgreSQL с https://www.postgresql.org/download/windows/
2. Установите по умолчанию (порт 5432, пользователь postgres)

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

### macOS
```bash
brew install postgresql
brew services start postgresql
```

## Шаг 2: Создайте базу данных и пользователя

### Через командную строку:

```bash
# Подключитесь к PostgreSQL
psql -U postgres

# Выполните SQL команды:
CREATE USER myuser WITH PASSWORD '12345678';
CREATE DATABASE todolist OWNER myuser;
GRANT ALL PRIVILEGES ON DATABASE todolist TO myuser;

# Выйдите
\q
```

### Или через pgAdmin:
1. Откройте pgAdmin
2. Правой кнопкой на "Databases" -> "Create" -> "Database"
3. Имя: `todolist`
4. Owner: `myuser`

## Шаг 3: Создайте таблицы

### Автоматически через Spring
Запустите приложение, и Spring создаст таблицы автоматически (если `generateDdl=true`).

### Или вручную через psql:

```bash
psql -U myuser -d todolist
```

Затем выполните:

```sql
CREATE TABLE IF NOT EXISTS todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Шаг 4: Проверьте подключение

Откройте браузер и перейдите по адресу:

```
http://localhost:8080/app/test/db-status
```

Ожидаемый ответ:

```
Database connection test: OK
Service bean: com.example.todolist.service.TodoService
```

## Шаг 5: Проверьте, что таблицы созданы

```bash
psql -U myuser -d todolist -c "\dt"
```

Ожидаемый ответ:

```
           List of relations
 Schema | Name  | Type  |  Owner  
--------+-------+-------+---------
 public | todos | table | myuser
```

## Шаг 6: Добавьте первую задачу

### Через curl:

```bash
curl -X POST http://localhost:8080/app/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Моя первая задача",
    "description": "Это тестовая задача",
    "status": "PENDING"
  }'
```

### Через браузер:
1. Откройте DevTools (F12)
2. Вкладка "Network"
3. В адресной строке: `http://localhost:8080/app/api/todos`
4. Нажмите Enter, затем выберите POST
5. В теле запроса вставьте JSON выше
6. Нажмите "Send"

## Шаг 7: Проверьте, что задача добавлена

### Через psql:

```bash
psql -U myuser -d todolist -c "SELECT * FROM todos;"
```

### Через браузер:

```
http://localhost:8080/app/api/todos
```

## Возможные проблемы и решения

### Проблема: "Database connection failed"

**Причина**: PostgreSQL не запущен или неправильные учетные данные

**Решение**:
```bash
# Проверьте, что PostgreSQL запущен
# Windows: net start postgresql
# Linux: sudo systemctl status postgresql
# macOS: brew services list | grep postgresql

# Проверьте учетные данные в application-context.xml
```

### Проблема: "Table does not exist"

**Причина**: Таблицы не созданы

**Решение**:
1. Убедитесь, что `generateDdl=true` в `application-context.xml`
2. Или создайте таблицы вручную (см. Шаг 3)

### Проблема: "permission denied for table todos"

**Причина**: Пользователь не имеет прав на таблицу

**Решение**:
```sql
GRANT ALL PRIVILEGES ON TABLE todos TO myuser;
GRANT ALL PRIVILEGES ON SEQUENCE todos_id_seq TO myuser;
```

### Проблема: "relation 'todos' already exists"

**Причина**: Таблица уже существует, но `generateDdl` пытается создать её снова

**Решение**:
1. Установите `generateDdl=false` в `application-context.xml`
2. Или удалите таблицу вручную

## Проверка работы приложения

1. **Получение всех задач**: `http://localhost:8080/app/api/todos`
2. **Добавить задачу**: POST `http://localhost:8080/app/api/todos`
3. **Обновить задачу**: PUT `http://localhost:8080/app/api/todos/{id}`
4. **Удалить задачу**: DELETE `http://localhost:8080/app/api/todos/{id}`
5. **Задачи по статусу**: GET `http://localhost:8080/app/api/todos/status/{status}`

## Логи приложения

Если данные не сохраняются, проверьте логи:

- **Maven**: смотрите в консоль при запуске
- **Tomcat**: файлы в `logs/` или `catalina.out`
- **PostgreSQL**: `pg_log` в базе данных

Пример команды для просмотра логов PostgreSQL:

```bash
sudo tail -f /var/log/postgresql/postgresql-14-main.log
```

