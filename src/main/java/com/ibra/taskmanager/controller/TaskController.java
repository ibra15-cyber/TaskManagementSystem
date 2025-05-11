package com.ibra.taskmanager.controller;


import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskPriority;
import com.ibra.taskmanager.enums.TaskStatus;
import com.ibra.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public String getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search,
            Model model) {

        List<Task> tasks;

        // Apply search if provided
        if (search != null && !search.isEmpty()) {
            tasks = taskService.searchTasks(search);
            model.addAttribute("search", search);
        }
        // Apply filters if provided
        else {
            // Get initial tasks based on status filter if provided
            if (status != null) {
                tasks = taskService.getTasksByStatus(status);
                model.addAttribute("selectedStatus", status);
            } else {
                tasks = taskService.getAllTasks();
            }

            // Further filter by priority if provided
            if (priority != null) {
                tasks = tasks.stream()
                        .filter(task -> task.getPriority() == priority)
                        .collect(Collectors.toList());
                model.addAttribute("selectedPriority", priority);
            }

            // Further filter by assignee if provided
            if (assignee != null && !assignee.isEmpty()) {
                tasks = tasks.stream()
                        .filter(task -> assignee.equals(task.getAssignee()))
                        .collect(Collectors.toList());
                model.addAttribute("selectedAssignee", assignee);
            }

            // Apply sorting if provided
            if ("dueDate".equals(sort)) {
                tasks = tasks.stream()
                        .sorted((t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate()))
                        .collect(Collectors.toList());
            } else if ("dueDateDesc".equals(sort)) {
                tasks = tasks.stream()
                        .sorted((t1, t2) -> t2.getDueDate().compareTo(t1.getDueDate()))
                        .collect(Collectors.toList());
            } else if ("priority".equals(sort)) {
                tasks = tasks.stream()
                        .sorted((t1, t2) -> comparePriority(t1.getPriority(), t2.getPriority()))
                        .collect(Collectors.toList());
            }
        }

        // Get all unique assignees for filtering
        List<String> assignees = taskService.getAllTasks().stream()
                .map(Task::getAssignee)
                .filter(a -> a != null && !a.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("tasks", tasks);
        model.addAttribute("sort", sort);
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("assignees", assignees);
        return "task/list";
    }

    // Helper method to compare priorities (HIGH > MEDIUM > LOW)
    private int comparePriority(TaskPriority p1, TaskPriority p2) {
        // Return higher priority first
        return Integer.compare(getPriorityValue(p2), getPriorityValue(p1));
    }

    private int getPriorityValue(TaskPriority priority) {
        switch (priority) {
            case HIGH: return 3;
            case MEDIUM: return 2;
            case LOW: return 1;
            default: return 0;
        }
    }

    // Show form for creating a new task
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("statuses", TaskStatus.values());
        model.addAttribute("priorities", TaskPriority.values());
        return "task/form";
    }

    // Process form submission for creating a new task
    @PostMapping
    public String createTask(@Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "task/form";
        }

        taskService.createTask(task);
        redirectAttributes.addFlashAttribute("message", "Task created successfully!");


        return "redirect:/tasks";
    }

    // Show form for editing an existing task
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (taskOptional.isPresent()) {
            model.addAttribute("task", taskOptional.get());
            model.addAttribute("statuses", TaskStatus.values());
            model.addAttribute("priorities", TaskPriority.values());
            return "task/form";
        } else {
            return "redirect:/tasks";
        }
    }

    // Process form submission for updating an existing task
    @PostMapping("/{id}")
    public String updateTask(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("task") Task task,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "task/form";
        }

        task.setId(id);
        taskService.updateTask(task);
        redirectAttributes.addFlashAttribute("message", "Task updated successfully!");
        return "redirect:/tasks";
    }

    // View a specific task's details
    @GetMapping("/{id}")
    public String viewTask(@PathVariable("id") Long id, Model model) {
        Optional<Task> taskOptional = taskService.getTaskById(id);

        if (taskOptional.isPresent()) {
            model.addAttribute("task", taskOptional.get());
            return "task/view";
        } else {
            return "redirect:/tasks";
        }
    }

    // Delete a task
    @GetMapping("/{id}/delete")
    public String deleteTask(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("message", "Task deleted successfully!");
        return "redirect:/tasks";
    }

    // Mark a task as completed
    @GetMapping("/{id}/complete")
    public String markAsCompleted(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        taskService.markTaskAsCompleted(id);
        redirectAttributes.addFlashAttribute("message", "Task marked as completed!");
        return "redirect:/tasks";
    }

    // Mark a task as in progress
    @GetMapping("/{id}/inprogress")
    public String markAsInProgress(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        taskService.markTaskAsInProgress(id);
        redirectAttributes.addFlashAttribute("message", "Task marked as in progress!");
        return "redirect:/tasks";
    }
}