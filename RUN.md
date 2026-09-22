# Как запустить проект без Maven

## Проблема

Maven не установлен в системе, но Java есть.

## Решение

Соберём WAR-файл вручную и запустим в Tomcat.

---

## Шаг 1: Установка Maven

### Вариант 1: Через apt (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install maven
```

### Вариант 2: Скачать с сайта

1. Перейдите на https://maven.apache.org/download.cgi
2. Скачайте maven-3.9.x-bin.tar.gz
3. Распакуйте:
```bash
tar -xzf apache-maven-3.9.9-bin.tar.gz
export MAVEN_HOME=/path/to/apache-maven-3.9.9
export PATH=$MAVEN_HOME/bin:$PATH
```

### Вариант 3: Использовать mvnw (если есть)

```bash
./mvnw clean package
```

---

## Шаг 2: Сборка без Maven

Создайте скрипт `build.sh`:

```bash
#!/bin/bash

# Создаём директорию
mkdir -p target

# Создаём WAR-файл
jar -cf target/todo-list.war \
    -C src/main/java/ . \
    -C src/main/resources/ . \
    -C src/main/webapp/ .

echo "✓ WAR-файл создан: target/todo-list.war"
```

Запустите:

```bash
chmod +x build.sh
./build.sh
```

---

## Шаг 3: Установка Tomcat

### Скачать Tomcat

1. Перейдите на https://tomcat.apache.org/download-10.cgi
2. Скачайте tomcat-10.1.x.tar.gz

### Установка

```bash
tar -xzf tomcat-10.1.30.tar.gz
cd tomcat-10.1.30
export CATALINA_HOME=/path/to/tomcat
export PATH=$CATALINA_HOME/bin:$PATH
```

### Запуск Tomcat

```bash
# Запустить
$CATALINA_HOME/bin/startup.sh

# Проверить
$CATALINA_HOME/bin/status.sh

# Остановить
$CATALINA_HOME/bin/shutdown.sh
```

---

## Шаг 4: Развёртывание WAR-файла

### Копируем WAR в Tomcat

```bash
# Создаём директорию для приложений
mkdir -p $CATALINA_HOME/webapps

# Копируем WAR-файл
cp target/todo-list.war $CATALINA_HOME/webapps/

# Переименовываем в todo-list.war (без расширения для авто-развёртывания)
mv target/todo-list.war $CATALINA_HOME/webapps/todo-list.war
```

### Перезапускаем Tomcat

```bash
$CATALINA_HOME/bin/startup.sh
```

---

## Шаг 5: Проверка

Откройте в браузере:

```
http://localhost:8080/todo-list/
```

Или через curl:

```bash
curl http://localhost:8080/todo-list/api/todos
```

---

## Шаг 6: Установка MySQL

### Linux (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install mysql-server

# Установите пароль для root
sudo mysql_secure_installation
```

### Windows

1. Скачайте https://dev.mysql.com/downloads/installer/
2. Установите MySQL
3. Запомните пароль root

---

## Шаг 7: Настройка подключения

Откройте файл `src/main/java/com/example/todolist/config/AppConfig.java`

Измените конфигурацию:

```java
@Bean
public DataSource dataSource() {
    DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds.setUrl("jdbc:mysql://localhost:3306/todolist?useSSL=false&serverTimezone=UTC");
    ds.setUsername("root");
    ds.setPassword("ВАШ_ПАРОЛЬ"); // Ваш пароль MySQL
    return ds;
}
```

---

## Альтернатива: Запуск через Java

Если Tomcat не нужен, можно запустить Spring через Java:

Создайте файл `run.sh`:

```bash
#!/bin/bash

# Создаём класс запуска
cat > RunTodoApp.java << 'JAVA'
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunTodoApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = 
            new ClassPathXmlApplicationContext("application-context.xml");
        context.start();
        
        System.out.println("Spring запущен!");
        
        // Удерживаем
        try {
            java.io.BufferedReader reader = 
                new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            reader.readLine();
        } catch (Exception e) {}
        
        context.close();
    }
}
JAVA

# Компилируем
javac -cp "target/todo-list.war:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':')" RunTodoApp.java

# Запускаем
java -cp ".:target/todo-list.war:$(find ~/.m2/repository -name '*.jar' | tr '\n' ':')" RunTodoApp
```

---

## Полный список команд

```bash
# 1. Установка Maven
sudo apt install maven

# 2. Сборка проекта
mvn clean package

# 3. Запуск через Maven (самый простой)
mvn spring-boot:run

# 4. Или через Tomcat
cp target/todo-list.war $CATALINA_HOME/webapps/
$CATALINA_HOME/bin/startup.sh

# 5. Проверка
curl http://localhost:8080/todo-list/api/todos
```

---

## Если ничего не помогает

Используйте Docker:

```bash
docker run -p 8080:8080 \
  -e MYSQL_ROOT_PASSWORD=yourpassword \
  -e MYSQL_DATABASE=todolist \
  tomcat:10.1-jdk21
```

---

## Тестирование API

```bash
# Создать задачу
curl -X POST http://localhost:8080/todo-list/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин"}'

# Получить все задачи
curl http://localhost:8080/todo-list/api/todos

# Обновить задачу
curl -X PUT http://localhost:8080/todo-list/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Купить хлеб", "description": "В магазин", "status": "COMPLETED"}'

# Удалить задачу
curl -X DELETE http://localhost:8080/todo-list/api/todos/1
```
