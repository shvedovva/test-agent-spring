package com.example.todolist.repository;

import com.example.todolist.model.Todo;
import com.example.todolist.model.TodoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository для работы с Todo сущностями.
 * 
 * JpaRepository предоставляет базовые CRUD операции автоматически.
 * 
 * @author Developer
 * @version 1.0
 */
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    
    /**
     * Найти все задачи.
     */
    List<Todo> findAll();
    
    /**
     * Найти задачу по ID.
     */
    Optional<Todo> findById(Long id);
    
    /**
     * Найти все задачи по статусу.
     */
    List<Todo> findByStatus(TodoStatus status);
    
    /**
     * Удалить задачу по ID.
     */
    void deleteById(Long id);
}
