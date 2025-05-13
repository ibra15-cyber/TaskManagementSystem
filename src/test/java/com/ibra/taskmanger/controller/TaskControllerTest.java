package com.ibra.taskmanger.controller;

import com.ibra.taskmanager.controller.TaskController;
import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskPriority;
import com.ibra.taskmanager.enums.TaskStatus;
import com.ibra.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

        task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setPriority(TaskPriority.MEDIUM);
        task1.setDueDate(LocalDateTime.now().plusDays(7));

        task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.COMPLETED);
        task2.setPriority(TaskPriority.HIGH);
        task2.setDueDate(LocalDateTime.now().plusDays(14));
    }


    @Test
    void testGetAllTasks_StatusFilter() throws Exception {
        when(taskService.getTasksByStatus(TaskStatus.COMPLETED)).thenReturn(Collections.singletonList(task2));

        mockMvc.perform(get("/tasks").param("status", TaskStatus.COMPLETED.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attribute("tasks", Collections.singletonList(task2)))
                .andExpect(model().attribute("selectedStatus", TaskStatus.COMPLETED));
    }

    @Test
    void testGetAllTasks_PriorityFilter() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("priority", TaskPriority.HIGH.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("selectedPriority", TaskPriority.HIGH));
    }

    @Test
    void testGetAllTasks_AssigneeFilter() throws Exception {
        task1.setAssignee("John Doe");
        task2.setAssignee("Jane Smith");
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("assignee", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("selectedAssignee", "John Doe"));
    }

    @Test
    void testGetAllTasks_SortByDueDate() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("sort", "dueDate"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("sort", "dueDate"));
    }

    @Test
    void testGetAllTasks_SortByDueDateDesc() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("sort", "dueDateDesc"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("sort", "dueDateDesc"));
    }

    @Test
    void testGetAllTasks_SortByPriority() throws Exception {
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("sort", "priority"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attribute("sort", "priority"));
    }

    @Test
    void testGetAllTasks_Search() throws Exception {
        when(taskService.searchTasks("Task")).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks").param("search", "Task"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/list"))
                .andExpect(model().attribute("tasks", Arrays.asList(task1, task2)))
                .andExpect(model().attribute("search", "Task"));
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/form"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    void testCreateTask_ValidInput() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(task1);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Task")
                        .param("description", "Description")
                        .param("dueDate", LocalDateTime.now().plusDays(7).toString())
                        .param("status", TaskStatus.PENDING.toString())
                        .param("priority", TaskPriority.MEDIUM.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(flash().attributeExists("message"));

        verify(taskService).createTask(any(Task.class));
    }


    @Test
    void testShowEditForm_TaskFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

        mockMvc.perform(get("/tasks/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/form"))
                .andExpect(model().attribute("task", task1))
                .andExpect(model().attributeExists("statuses"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    void testShowEditForm_TaskNotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void testUpdateTask_ValidInput() throws Exception {
        when(taskService.updateTask(any(Task.class))).thenReturn(task1);

        mockMvc.perform(post("/tasks/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Updated Task")
                        .param("description", "Updated Description")
                        .param("dueDate", LocalDateTime.now().plusDays(14).toString())
                        .param("status", TaskStatus.COMPLETED.toString())
                        .param("priority", TaskPriority.HIGH.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(flash().attributeExists("message"));

        verify(taskService).updateTask(any(Task.class));
    }


    @Test
    void testViewTask_TaskFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("task/view"))
                .andExpect(model().attribute("task", task1));
    }

    @Test
    void testViewTask_TaskNotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(get("/tasks/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(flash().attributeExists("message"));

        verify(taskService).deleteTask(1L);
    }

    @Test
    void testMarkAsCompleted() throws Exception {
        doNothing().when(taskService).markTaskAsCompleted(1L);

        mockMvc.perform(get("/tasks/1/complete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(flash().attributeExists("message"));

        verify(taskService).markTaskAsCompleted(1L);
    }

    @Test
    void testMarkAsInProgress() throws Exception {
        doNothing().when(taskService).markTaskAsInProgress(1L);

        mockMvc.perform(get("/tasks/1/inprogress"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(flash().attributeExists("message"));

        verify(taskService).markTaskAsInProgress(1L);
    }
}