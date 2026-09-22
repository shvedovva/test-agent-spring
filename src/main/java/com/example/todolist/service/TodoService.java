package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.model.TodoStatus;
import com.example.todolist.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для работы с задачами.
 * 
 * @author Developer
 * @version 1.0
 */
@Service
@Transactional
public class TodoService {
    
    /**
     * Инъекция Repository через Spring Container.
     * Spring сам создаст экземпляр и вставит его сюда.
     */
    private final TodoRepository todoRepository;
    
    /**
     * Конструктор с Dependency Injection.
     */
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }
    
    /**
     * Получить все задачи.
     */
    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }
    
    /**
     * Получить задачу по ID.
     */
    @Transactional(readOnly = true)
    public Todo getTodoById(Long id) {
        return todoRepository.findById(id).get();
    }
    
    /**
     * Создать новую задачу.
     */
    @Transactional
    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }
    
    /**
     * Обновить задачу.
     */
    @Transactional
    public Todo updateTodo(Long id, Todo updatedTodo) {
        Todo existingTodo = todoRepository.findById(id).get();
        if (existingTodo == null) {
            throw new RuntimeException("Task not found: " + id);
        }
        
        // Обновляем поля
        existingTodo.setTitle(updatedTodo.getTitle());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setStatus(updatedTodo.getStatus());
        
        return todoRepository.save(existingTodo);
    }
    
    /**
     * Удалить задачу.
     */
    @Transactional
    public void deleteTodo(Long id) {
        todoRepository.deleteById(id);
    }
    
    /**
     * Получить задачи по статусу.
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByStatus(TodoStatus status) {
        return todoRepository.findByStatus(status);
    }
}
