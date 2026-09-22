-- Скрипт для настройки базы данных PostgreSQL для Todo List приложения

-- Создаем базу данных (если не существует)
CREATE DATABASE todolist 
    WITH 
    OWNER = myuser
    ENCODING = 'UTF8'
    LC_COLLATE = 'Russian_Russia.1251'
    LC_CTYPE = 'Russian_Russia.1251'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Создаем пользователя (если не существует)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'myuser') THEN
        CREATE ROLE myuser WITH LOGIN PASSWORD '12345678';
    END IF;
END $$;

-- Присваиваем права на базу данных
GRANT ALL PRIVILEGES ON DATABASE todolist TO myuser;

-- Подключаемся к базе данных и создаем таблицы
\c todolist

-- Создает таблицы автоматически (если generateDdl=true в конфигурации Spring)
-- Но если таблицы не созданы, можно создать вручную:

CREATE TABLE IF NOT EXISTS todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Присваиваем права на таблицы
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO myuser;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO myuser;
