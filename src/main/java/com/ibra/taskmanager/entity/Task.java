package com.ibra.taskmanager.entity;

import com.ibra.taskmanager.enums.TaskPriority;
import com.ibra.taskmanager.enums.TaskStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    @Column(nullable = false)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @Column(name = "assignee", length = 100)
    private String assignee;

    // Default constructor
    public Task() {
        this.status = TaskStatus.PENDING;
        this.priority = TaskPriority.MEDIUM;
    }

    // Constructor with fields
    public Task(String title, String description, LocalDateTime dueDate) {
        this();
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", priority=" + priority +
                ", assignee='" + assignee + '\'' +
                '}';
    }
}
