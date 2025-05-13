package com.ibra.taskmanger.task;

import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskPriority;
import com.ibra.taskmanager.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {

    private Task task;
    private LocalDateTime dueDate;

    @BeforeEach
    void setUp() {
        dueDate = LocalDateTime.now().plusDays(7);
        task = new Task("Test Task", "This is a test", dueDate);
    }

    @Test
    void testDefaultConstructor() {
        Task defaultTask = new Task();
        assertNotNull(defaultTask.getStatus());
        assertEquals(TaskStatus.PENDING, defaultTask.getStatus());
        assertNotNull(defaultTask.getPriority());
        assertEquals(TaskPriority.MEDIUM, defaultTask.getPriority());
    }

    @Test
    void testConstructorWithFields() {
        assertEquals("Test Task", task.getTitle());
        assertEquals("This is a test", task.getDescription());
        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void testGettersAndSetters() {
        task.setTitle("Updated Task");
        assertEquals("Updated Task", task.getTitle());

        task.setDescription("Updated description");
        assertEquals("Updated description", task.getDescription());

        LocalDateTime newDueDate = LocalDateTime.now().plusDays(14);
        task.setDueDate(newDueDate);
        assertEquals(newDueDate, task.getDueDate());

        task.setStatus(TaskStatus.COMPLETED);
        assertEquals(TaskStatus.COMPLETED, task.getStatus());

        task.setPriority(TaskPriority.HIGH);
        assertEquals(TaskPriority.HIGH, task.getPriority());

        task.setAssignee("John Doe");
        assertEquals("John Doe", task.getAssignee());
    }
}
