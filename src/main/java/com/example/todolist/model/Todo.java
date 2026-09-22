package com.example.todolist.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Модель задачи (Todo).
 * 
 * @author Developer
 * @version 1.0
 */
@Entity
@Table(name = "todos")
public class Todo {
    
    // ID задачи (первичный ключ)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Название задачи
    @Column(nullable = false, length = 200)
    private String title;
    
    // Описание задачи
    @Column(length = 500)
    private String description;
    
    // Статус выполнения
    @Enumerated(EnumType.STRING)
    private TodoStatus status;
    
    // Дата создания
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    // Дата обновления
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Конструктор без аргументов (требован JPA)
    public Todo() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TodoStatus.PENDING;
    }
    
    // Конструктор с данными
    public Todo(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TodoStatus getStatus() { return status; }
    public void setStatus(TodoStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now(); 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Todo{id=" + id + ", title='" + title + "', status=" + status + "}";
    }
}
