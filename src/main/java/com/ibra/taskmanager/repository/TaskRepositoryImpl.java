package com.ibra.taskmanager.repository;

import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Transactional
public class TaskRepositoryImpl implements TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Task save(Task task) {
        if (task == null) {
            logger.warn("Attempted to save a null task.");
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            logger.warn("Task title cannot be null or empty.");
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        try {
            entityManager.persist(task);
            logger.info("Task saved successfully: {}", task);
            return task;
        } catch (Exception e) {
            logger.error("Error saving task: {}", task, e);
            throw new RuntimeException("Error saving task", e);
        }
    }

    @Override
    public Optional<Task> findById(Long id) {
        if (id == null) {
            logger.warn("Attempted to find task with null id.");
            throw new IllegalArgumentException("ID cannot be null");
        }
        try {
            Task task = entityManager.find(Task.class, id);
            if (task != null) {
                logger.info("Task found with id: {}", id);
            } else {
                logger.warn("Task not found with id: {}", id);
            }
            return Optional.ofNullable(task);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for findById", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public List<Task> findAll() {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);
            cq.select(root);
            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found all tasks. Total: {}", tasks.size());
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding all tasks", e);
            throw new RuntimeException("Error finding all tasks", e);
        }
    }

    @Override
    public void delete(Task task) {
        if (task == null) {
            logger.warn("Attempted to delete a null task.");
            throw new IllegalArgumentException("Task cannot be null");
        }
        try {
            entityManager.remove(task);
            logger.info("Task deleted successfully: {}", task);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for delete", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            logger.warn("Attempted to delete task with null id.");
            throw new IllegalArgumentException("ID cannot be null");
        }
        try {
            Task task = entityManager.find(Task.class, id);
            if (task != null) {
                entityManager.remove(task);
                logger.info("Task deleted successfully with id: {}", id);
            } else {
                logger.warn("Task not found for deletion with id: {}", id);
                throw new IllegalArgumentException("Task not found with id: " + id);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for deleteById", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        if (status == null) {
            logger.warn("Attempted to find tasks by null status.");
            throw new IllegalArgumentException("Status cannot be null");
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            cq.select(root)
                    .where(cb.equal(root.get("status"), status));

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found tasks by status: {}. Total: {}", status, tasks.size());
            return tasks;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for findByStatus", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public List<Task> findByDueDateBefore(LocalDateTime date) {
        if (date == null) {
            logger.warn("Attempted to find tasks by null due date.");
            throw new IllegalArgumentException("Date cannot be null");
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            cq.select(root)
                    .where(cb.lessThan(root.get("dueDate"), date));

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found tasks by due date before: {}. Total: {}", date, tasks.size());
            return tasks;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for findByDueDateBefore", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public List<Task> findByDueDateAfter(LocalDateTime date) {
        if (date == null) {
            logger.warn("Attempted to find tasks by null due date.");
            throw new IllegalArgumentException("Date cannot be null");
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            cq.select(root)
                    .where(cb.greaterThan(root.get("dueDate"), date));

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found tasks by due date after: {}. Total: {}", date, tasks.size());
            return tasks;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for findByDueDateAfter", e);
            throw new IllegalArgumentException("Invalid argument", e);
        }
    }

    @Override
    public List<Task> findAllSortedByDueDate(boolean ascending) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            cq.select(root);

            if (ascending) {
                cq.orderBy(cb.asc(root.get("dueDate")));
            } else {
                cq.orderBy(cb.desc(root.get("dueDate")));
            }

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found all tasks sorted by due date (ascending: {}). Total: {}", ascending, tasks.size());
            return tasks;
        } catch (Exception e) {
            logger.error("Error finding all tasks sorted by due date", e);
            throw new RuntimeException("Error finding all tasks sorted by due date", e);
        }
    }

    @Override
    public List<Task> findByStatusAndSortByDueDate(TaskStatus status, boolean ascending) {
        if (status == null) {
            logger.warn("Attempted to find tasks by status and sort with null status.");
            throw new IllegalArgumentException("Status cannot be null");
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            cq.select(root)
                    .where(cb.equal(root.get("status"), status));

            if (ascending) {
                cq.orderBy(cb.asc(root.get("dueDate")));
            } else {
                cq.orderBy(cb.desc(root.get("dueDate")));
            }

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Found tasks by status: {} and sorted by due date (ascending: {}). Total: {}", status, ascending, tasks.size());
            return tasks;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument for findByStatusAndSortByDueDate", e);
            throw new IllegalArgumentException("Invalid argument", e);
        } catch (Exception e) {
            logger.error("Error in findByStatusAndSortByDueDate", e);
            throw new RuntimeException("Error in findByStatusAndSortByDueDate", e);
        }
    }

    @Override
    public List<Task> searchByTitleOrDescription(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.warn("Attempted to search tasks with null or empty keyword.");
            throw new IllegalArgumentException("Keyword cannot be null or empty");
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Task> cq = cb.createQuery(Task.class);
            Root<Task> root = cq.from(Task.class);

            String likePattern = "%" + keyword + "%";

            Predicate titlePredicate = cb.like(cb.lower(root.get("title")), likePattern.toLowerCase());
            Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), likePattern.toLowerCase());

            cq.select(root)
                    .where(cb.or(titlePredicate, descriptionPredicate));

            List<Task> tasks = entityManager.createQuery(cq).getResultList();
            logger.info("Searched tasks by keyword: '{}'. Total: {}", keyword, tasks.size());
            return tasks;
        } catch (Exception e) {
            logger.error("Error searching tasks by keyword: '{}'", keyword, e);
            throw new RuntimeException("Error searching tasks", e);
        }
    }
}