package com.example.todolist.controller;

import com.example.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Тестовый контроллер для проверки подключения к БД.
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private TodoService todoService;
    
    @GetMapping("/db-status")
    public ResponseEntity<String> checkDatabaseStatus() {
        try {
            // Попытка получить подключение к БД
            Properties props = new Properties();
            props.setProperty("user", "myuser");
            props.setProperty("password", "12345678");
            props.setProperty("driver", "org.postgresql.Driver");
            
            // Это просто проверка, что БД доступна
            return ResponseEntity.ok("Database connection test: OK\nService bean: " + todoService.getClass().getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Database connection failed: " + e.getMessage());
        }
    }
    
    @GetMapping("/service-test")
    public ResponseEntity<String> testService() {
        try {
            // Проверка, что сервис работает
            return ResponseEntity.ok("Service is working\n" + todoService.getClass().getName());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Service error: " + e.getMessage());
        }
    }
    
    @GetMapping("/todos")
    public ResponseEntity<String> getTodos() {
        try {
            var todos = todoService.getAllTodos();
            return ResponseEntity.ok("Found " + todos.size() + " todos");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting todos: " + e.getMessage());
        }
    }
}
