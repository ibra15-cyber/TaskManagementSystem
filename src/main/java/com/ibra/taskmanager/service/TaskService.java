package com.ibra.taskmanager.service;


import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    // Basic CRUD operations
    Task createTask(Task task);
    Task updateTask(Task task);
    Optional<Task> getTaskById(Long id);
    List<Task> getAllTasks();
    void deleteTask(Long id);

    // Filter and sort operations
    List<Task> getTasksByStatus(TaskStatus status);
    List<Task> getOverdueTasks();
    List<Task> getUpcomingTasks();
    List<Task> getTasksSortedByDueDate(boolean ascending);

    // Combined filter and sort
    List<Task> getTasksByStatusSortedByDueDate(TaskStatus status, boolean ascending);

    // Search by title or description
    List<Task> searchTasks(String keyword);

    // Status operations
    void markTaskAsCompleted(Long id);
    void markTaskAsInProgress(Long id);
}