package com.ibra.taskmanager.service;

import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;
import com.ibra.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.PENDING);

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.COMPLETED);
    }

    @Test
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task createdTask = taskService.createTask(new Task());

        verify(taskRepository).save(any(Task.class));
        assertEquals(task1, createdTask);
    }

    @Test
    void testUpdateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        Task updatedTask = taskService.updateTask(task1);

        verify(taskRepository).save(task1);
        assertEquals(task1, updatedTask);
    }

    @Test
    void testGetTaskById_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        verify(taskRepository).findById(1L);
        assertTrue(foundTask.isPresent());
        assertEquals(task1, foundTask.get());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> foundTask = taskService.getTaskById(1L);

        verify(taskRepository).findById(1L);
        assertTrue(foundTask.isEmpty());
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<Task> allTasks = taskService.getAllTasks();

        verify(taskRepository).findAll();
        assertEquals(2, allTasks.size());
        assertEquals(task1, allTasks.get(0));
        assertEquals(task2, allTasks.get(1));
    }

    @Test
    void testGetAllTasks_Empty() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> allTasks = taskService.getAllTasks();

        verify(taskRepository).findAll();
        assertEquals(0, allTasks.size());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void testGetTasksByStatus() {
        when(taskRepository.findByStatus(TaskStatus.PENDING)).thenReturn(Collections.singletonList(task1));

        List<Task> pendingTasks = taskService.getTasksByStatus(TaskStatus.PENDING);

        verify(taskRepository).findByStatus(TaskStatus.PENDING);
        assertEquals(1, pendingTasks.size());
        assertEquals(task1, pendingTasks.get(0));
    }

    @Test
    void testGetOverdueTasks() {
        when(taskRepository.findByDueDateBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(task1));

        List<Task> overdueTasks = taskService.getOverdueTasks();

        verify(taskRepository).findByDueDateBefore(any(LocalDateTime.class));
        assertEquals(1, overdueTasks.size());
        assertEquals(task1, overdueTasks.get(0));
    }

    @Test
    void testGetUpcomingTasks() {
        when(taskRepository.findByDueDateAfter(any(LocalDateTime.class))).thenReturn(Collections.singletonList(task1));

        List<Task> upcomingTasks = taskService.getUpcomingTasks();

        verify(taskRepository).findByDueDateAfter(any(LocalDateTime.class));
        assertEquals(1, upcomingTasks.size());
        assertEquals(task1, upcomingTasks.get(0));
    }

    @Test
    void testGetTasksSortedByDueDate() {
        when(taskRepository.findAllSortedByDueDate(true)).thenReturn(Arrays.asList(task1, task2));

        List<Task> sortedTasks = taskService.getTasksSortedByDueDate(true);

        verify(taskRepository).findAllSortedByDueDate(true);
        assertEquals(2, sortedTasks.size());
        assertEquals(task1, sortedTasks.get(0));
        assertEquals(task2, sortedTasks.get(1));
    }

    @Test
    void testGetTasksByStatusSortedByDueDate() {
        when(taskRepository.findByStatusAndSortByDueDate(TaskStatus.PENDING, true)).thenReturn(Collections.singletonList(task1));

        List<Task> sortedPendingTasks = taskService.getTasksByStatusSortedByDueDate(TaskStatus.PENDING, true);

        verify(taskRepository).findByStatusAndSortByDueDate(TaskStatus.PENDING, true);
        assertEquals(1, sortedPendingTasks.size());
        assertEquals(task1, sortedPendingTasks.get(0));
    }

    @Test
    void testSearchTasks() {
        when(taskRepository.searchByTitleOrDescription("test")).thenReturn(Collections.singletonList(task1));

        List<Task> searchResults = taskService.searchTasks("test");

        verify(taskRepository).searchByTitleOrDescription("test");
        assertEquals(1, searchResults.size());
        assertEquals(task1, searchResults.get(0));
    }

    @Test
    void testMarkTaskAsCompleted_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        taskService.markTaskAsCompleted(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        assertEquals(TaskStatus.COMPLETED, task1.getStatus());
    }

    @Test
    void testMarkTaskAsCompleted_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        taskService.markTaskAsCompleted(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testMarkTaskAsInProgress_Found() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);

        taskService.markTaskAsInProgress(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
        assertEquals(TaskStatus.IN_PROGRESS, task1.getStatus());
    }

    @Test
    void testMarkTaskAsInProgress_NotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        taskService.markTaskAsInProgress(1L);

        verify(taskRepository).findById(1L);
        verify(taskRepository, never()).save(any(Task.class));
    }
}