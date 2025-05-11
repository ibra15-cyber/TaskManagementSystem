package com.ibra.taskmanager.service;

import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;
import com.ibra.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        List<Task> tasks =  taskRepository.findAll();

        for (Task task : tasks) {
            log.warn("Task....................", task);
        }
        return tasks;
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findByDueDateBefore(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getUpcomingTasks() {
        return taskRepository.findByDueDateAfter(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksSortedByDueDate(boolean ascending) {
        return taskRepository.findAllSortedByDueDate(ascending);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatusSortedByDueDate(TaskStatus status, boolean ascending) {
        return taskRepository.findByStatusAndSortByDueDate(status, ascending);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String keyword) {
        return taskRepository.searchByTitleOrDescription(keyword);
    }

    @Override
    public void markTaskAsCompleted(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        optionalTask.ifPresent(task -> {
            task.setStatus(TaskStatus.COMPLETED);
            taskRepository.save(task);
        });
    }

    @Override
    public void markTaskAsInProgress(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        optionalTask.ifPresent(task -> {
            task.setStatus(TaskStatus.IN_PROGRESS);
            taskRepository.save(task);
        });
    }
}
