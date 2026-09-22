# Тестирование Todo List приложения

## 1. Проверка подключения к PostgreSQL

### Убедитесь, что PostgreSQL запущен

```bash
# Windows
net start postgresql

# Linux
sudo systemctl status postgresql

# macOS
brew services list | grep postgresql
```

### Проверка подключения

Откройте приложение в браузере и перейдите по адресу:

```
http://localhost:8080/app/test/db-status
```

Ожидаемый ответ:

```
Database connection test: OK
Service bean: com.example.todolist.service.TodoService
```

## 2. Проверка работы сервиса

Перейдите по адресу:

```
http://localhost:8080/app/test/service-test
```

Ожидаемый ответ:

```
Service is working
com.example.todolist.service.TodoService
```

## 3. Проверка получения всех задач

Перейдите по адресу:

```
http://localhost:8080/app/test/todos
```

Ожидаемый ответ:

```
Found 0 todos
```

(если задач еще нет)

## 4. Добавление новой задачи

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

Ожидаемый ответ:

```json
{
    "id": 1,
    "title": "Моя первая задача",
    "description": "Это тестовая задача",
    "status": "PENDING",
    "createdAt": "2026-09-22T14:19:35.000Z",
    "updatedAt": "2026-09-22T14:19:35.000Z"
}
```

### Через браузер:

1. Откройте DevTools в браузере (F12)
2. Перейдите во вкладку "Network"
3. Введите в адресную строку:
   ```
   http://localhost:8080/app/api/todos
   ```
4. Нажмите Enter
5. Выберите тип запроса: POST
6. В теле запроса вставьте JSON:
   ```json
   {
     "title": "Моя первая задача",
     "description": "Это тестовая задача",
     "status": "PENDING"
   }
   ```
7. Нажмите "Send"

## 5. Проверка сохранения в БД

### Через psql:

```bash
psql -U myuser -d todolist
```

Затем выполните:

```sql
SELECT * FROM todos;
```

### Через pgAdmin:

1. Откройте pgAdmin
2. Перейдите в: `todolist -> Tables -> todos`
3. Нажмите `Data`
4. Вы увидите все задачи

## 6. Ошибки и их решение

### Ошибка: "Database connection failed"

**Причина**: PostgreSQL не запущен или неправильные учетные данные

**Решение**:
1. Проверьте, что PostgreSQL запущен
2. Проверьте username/password в `application-context.xml`
3. Убедитесь, что база данных `todolist` существует

### Ошибка: "Service error"

**Причина**: Проблема с сервисом или транзакциями

**Решение**:
1. Проверьте логи приложения
2. Убедитесь, что `@Transactional` работает корректно
3. Проверьте, что таблицы созданы

### Данные не сохраняются

**Причина**: Транзакция не коммитится

**Решение**:
1. Убедитесь, что метод `createTodo` имеет аннотацию `@Transactional`
2. Проверьте, что `@Transactional` на уровне класса работает корректно
3. Убедитесь, что PostgreSQL работает

## 7. Логи приложения

Проверьте логи приложения для отладки:

- Если запущено через Maven: смотрите в консоль
- Если развернуто в Tomcat: проверьте файлы в `logs/` или `catalina.out`

Пример ошибок:
- `SQLException` - проблема с подключением к БД
- `TransactionRollbackException` - транзакция откатилась
- `NullPointerException` - проблема с объектами
