# Решение проблемы: данные не сохраняются в БД

## Основная причина

Скорее всего **PostgreSQL не запущен** или **база данных не существует**.

## Быстрая проверка

```bash
# 1. Проверьте PostgreSQL
pg_isready -h localhost -p 5432

# 2. Проверьте базу данных
psql -U myuser -d todolist -c "SELECT 1;"

# 3. Проверьте таблицы
psql -U myuser -d todolist -c "\dt"
```

## Если PostgreSQL не запущен

### Windows
```bash
net start postgresql
```

### Linux
```bash
sudo systemctl start postgresql
```

### macOS
```bash
brew services start postgresql
```

## Если база данных не существует

```bash
psql -U postgres
CREATE DATABASE todolist OWNER myuser;
\q
```

## Если таблицы не созданы

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

## Проверка подключения к БД

Откройте браузер и перейдите по адресу:

```
http://localhost:8080/app/test/db-status
```

Ожидаемый ответ:

```
Database connection test: OK
Service bean: com.example.todolist.service.TodoService
```

## Если ошибка "Database connection failed"

**Причина**: PostgreSQL не запущен или неправильные учетные данные

**Решение**:
1. Проверьте, что PostgreSQL запущен
2. Проверьте username/password в `application-context.xml`
3. Убедитесь, что база данных `todolist` существует

## Если ошибка "Table does not exist"

**Причина**: Таблицы не созданы

**Решение**:
1. Убедитесь, что `generateDdl=true` в `application-context.xml`
2. Или создайте таблицы вручную (см. выше)

## Если данные не сохраняются после добавления

**Возможные причины**:

1. **Транзакция не коммитится**
   - Убедитесь, что метод `createTodo` имеет `@Transactional`
   - Убедитесь, что `@Transactional` на уровне класса работает корректно

2. **PostgreSQL не запущен**
   - Проверьте статус PostgreSQL

3. **База данных не существует**
   - Проверьте существование базы данных `todolist`

4. **Пользователь не имеет доступа**
   - Проверьте права пользователя `myuser`

5. **Таблицы не созданы**
   - Проверьте существование таблицы `todos`

## Проверка транзакций

Добавьте тестовый код в `TodoService`:

```java
@Transactional
public Todo createTodo(Todo todo) {
    Todo savedTodo = todoRepository.save(todo);
    System.out.println("Saved todo: " + savedTodo.getId());
    // Принудительный commit
    return savedTodo;
}
```

## Логи приложения

Проверьте логи для отладки:

```bash
# Tomcat
tail -f /path/to/tomcat/logs/catalina.out

# PostgreSQL
sudo tail -f /var/log/postgresql/postgresql-*.log
```

## Тестирование

### Добавить задачу:

```bash
curl -X POST http://localhost:8080/app/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Тестовая задача",
    "description": "Проверка добавления",
    "status": "PENDING"
  }'
```

### Проверить в БД:

```bash
psql -U myuser -d todolist -c "SELECT * FROM todos;"
```

### Проверить через браузер:

```
http://localhost:8080/app/api/todos
```

## Полный чек-лист

1. ✅ PostgreSQL запущен
2. ✅ База данных `todolist` существует
3. ✅ Пользователь `myuser` имеет доступ
4. ✅ Таблица `todos` создана
5. ✅ Приложение запущено
6. ✅ Подключение к БД работает
7. ✅ Данные сохраняются

## Дополнительные ресурсы

- [POSTGRESQL-SETUP.md](./POSTGRESQL-SETUP.md) - Подробная инструкция по настройке PostgreSQL
- [TESTING.md](./TESTING.md) - Инструкция по тестированию приложения
- [README-database-setup.md](./README-database-setup.md) - Настройка базы данных

