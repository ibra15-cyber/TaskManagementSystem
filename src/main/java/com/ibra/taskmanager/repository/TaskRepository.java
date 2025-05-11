package com.ibra.taskmanager.repository;



import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    // Basic CRUD operations
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    void delete(Task task);
    void deleteById(Long id);

    // Filter and sort operations
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByDueDateBefore(LocalDateTime date);
    List<Task> findByDueDateAfter(LocalDateTime date);
    List<Task> findAllSortedByDueDate(boolean ascending);

    // Combined filter and sort
    List<Task> findByStatusAndSortByDueDate(TaskStatus status, boolean ascending);

    // Search by title or description
    List<Task> searchByTitleOrDescription(String keyword);
}