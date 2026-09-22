package com.example.todolist.controller;

import com.example.todolist.model.Todo;
import com.example.todolist.model.TodoStatus;
import com.example.todolist.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с задачами.
 * 
 * @author Developer
 * @version 1.0
 */
@RestController
// Полный URL: /api/todos (префикс /api/* задаётся DispatcherServlet в web.xml)
@RequestMapping("/todos")
public class TodoController {
    
    /**
     * Инъекция Service через Spring Container.
     */
    @Autowired
    private TodoService todoService;
    
    /**
     * GET /api/todos - получить все задачи.
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }
    
    /**
     * GET /api/todos/{id} - получить задачу по ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.getTodoById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }
    
    /**
     * POST /api/todos - создать новую задачу.
     */
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }
    
    /**
     * PUT /api/todos/{id} - обновить задачу.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        Todo updated = todoService.updateTodo(id, updatedTodo);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * DELETE /api/todos/{id} - удалить задачу.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/todos/status/{status} - получить задачи по статусу.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Todo>> getTodosByStatus(@PathVariable String status) {
        TodoStatus todoStatus = TodoStatus.valueOf(status.toUpperCase());
        List<Todo> todos = todoService.getTodosByStatus(todoStatus);
        return ResponseEntity.ok(todos);
    }
}
