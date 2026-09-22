# Настройка базы данных для Todo List приложения

## PostgreSQL

### 1. Установите PostgreSQL

Скачайте и установите PostgreSQL с: https://www.postgresql.org/download/

### 2. Создайте базу данных и пользователя

Откройте терминал и выполните:

```bash
psql -U postgres
```

Затем выполните SQL команды:

```sql
-- Создаем базу данных
CREATE DATABASE todolist OWNER myuser;

-- Создаем пользователя
CREATE USER myuser WITH PASSWORD '12345678';

-- Присваиваем права
GRANT ALL PRIVILEGES ON DATABASE todolist TO myuser;

-- Выходим
\q
```

### 3. Запустите PostgreSQL сервер

```bash
# Windows
net start postgresql-x64-14

# Linux (Ubuntu/Debian)
sudo systemctl start postgresql

# macOS
brew services start postgresql
```

### 4. Создайте таблицы вручную (если не созданы автоматически)

```bash
psql -U myuser -d todolist -c "
CREATE TABLE IF NOT EXISTS todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
"
```

## Проверка подключения

```bash
psql -U myuser -d todolist -c "SELECT * FROM todos;"
```

## Тестирование добавления записи

Откройте браузер и перейдите по адресу: `http://localhost:8080/app/api/todos`

Добавьте новую запись:

```json
{
    "title": "Моя первая задача",
    "description": "Это тестовая задача",
    "status": "PENDING"
}
```

Ответ должен быть:

```json
{
    "id": 1,
    "title": "Моя первая задача",
    "description": "Это тестовая задача",
    "status": "PENDING",
    "createdAt": "2026-09-22T14:15:44.000Z",
    "updatedAt": "2026-09-22T14:15:44.000Z"
}
```

## Если данные не сохраняются

1. Проверьте логи приложения (консоль или log файл)
2. Убедитесь, что PostgreSQL запущен
3. Проверьте, что база данных `todolist` существует
4. Проверьте права пользователя `myuser`
5. Попробуйте выполнить SQL команды вручную через pgAdmin или psql
