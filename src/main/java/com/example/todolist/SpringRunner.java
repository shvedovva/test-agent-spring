package com.example.todolist;

import com.example.todolist.controller.TodoController;
import com.example.todolist.model.Todo;
import com.example.todolist.service.TodoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Класс для запуска Spring приложения.
 * 
 * В Spring Boot это делается через @SpringBootApplication,
 * здесь мы вручную создаём ApplicationContext.
 * 
 * @author Developer
 * @version 1.0
 */
public class SpringRunner {
    
    public static void main(String[] args) {
        System.out.println("=== Запуск Spring TodoList Application ===");
        System.out.println("Загрузка Spring ApplicationContext...");
        
        // Создаём и запускаем Spring Container
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        
        // Получаем сущности Spring
        TodoService service = context.getBean(TodoService.class);
        TodoController controller = context.getBean(TodoController.class);
        
        System.out.println("✓ Spring Container успешно запущен!");
        System.out.println("✓ Todoservice: " + service);
        System.out.println("✓ TodoController: " + controller);
        
        // Пример создания и сохранения задачи в БД
        Todo todo = new Todo("Привет, Spring!", "Это первый тестовый todo");
        System.out.println("\nСоздаём задачу: " + todo);
        try {
            Todo saved = service.createTodo(todo);
            System.out.println("✓ Сохранено в БД: " + saved);
            System.out.println("✓ Всего задач в БД: " + service.getAllTodos().size());
        } catch (Exception e) {
            System.out.println("✗ Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Запуск сервера Tomcat
        System.out.println("\nЗапуск Tomcat сервера...");
        context.start();
        
        System.out.println("✓ Приложение запущено!");
        System.out.println("✓ Сервер доступен на: http://localhost:8080");
        System.out.println("✓ API доступен на: http://localhost:8080/api/todos");
        
        // Удерживаем JVM
        System.out.println("\nНажмите Enter для завершения...");
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            reader.readLine();
        } catch (Exception e) {
            // ignore
        }
        
        System.out.println("Завершение приложения...");
        context.close();
    }
}
