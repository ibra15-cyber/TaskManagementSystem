package com.ibra.taskmanager.repository;

import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class TaskRepositoryImplTest {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImplTest.class);

    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction transaction;
    private TaskRepositoryImpl taskRepository;

    // Helper method to create a Task object
    private Task createTask(String title, String description, LocalDateTime dueDate, TaskStatus status) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setStatus(status);
        return task;
    }

    @BeforeAll
    public static void setupEntityManagerFactory() {
        // Use persistence.xml configuration, but specify H2 for in-memory testing
        emf = Persistence.createEntityManagerFactory("test-persistence-unit"); //Defined in test persistence.xml
        logger.info("EntityManagerFactory created for in-memory testing.");
    }

    @AfterAll
    public static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
            logger.info("EntityManagerFactory closed.");
        }
    }

    @BeforeEach
    public void setupEntityManagerAndTransaction() {
        em = emf.createEntityManager();
        transaction = em.getTransaction();
        transaction.begin();
        taskRepository = new TaskRepositoryImpl();
        // Inject the EntityManager
        taskRepository.entityManager = em;
        logger.info("EntityManager and transaction initialized.");
    }

    @AfterEach
    public void rollbackTransactionAndCloseEntityManager() {
        if (transaction.isActive()) {
            transaction.rollback();
            logger.info("Transaction rolled back.");
        }
        em.close();
        logger.info("EntityManager closed.");
    }

    @Test
    public void testSaveTask_success() {
        Task task = createTask("Test Task", "Test Description", LocalDateTime.now().plusDays(1), TaskStatus.PENDING);
        Task savedTask = taskRepository.save(task);
        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        logger.info("Test saveTask_success passed.");
    }

    @Test
    public void testSaveTask_nullTask() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.save(null));
        logger.info("Test saveTask_nullTask passed.");
    }

    @Test
    public void testSaveTask_nullTitle() {
        Task task = createTask(null, "Test Description", LocalDateTime.now().plusDays(1), TaskStatus.PENDING);
        assertThrows(IllegalArgumentException.class, () -> taskRepository.save(task));
        logger.info("Test saveTask_nullTitle passed.");
    }

    @Test
    public void testFindById_success() {
        Task task = createTask("Find Task", "Find Description", LocalDateTime.now().plusDays(2), TaskStatus.IN_PROGRESS);
        em.persist(task);
        Optional<Task> foundTask = taskRepository.findById(task.getId());
        assertTrue(foundTask.isPresent());
        assertEquals("Find Task", foundTask.get().getTitle());
        logger.info("Test findById_success passed.");
    }

    @Test
    public void testFindById_notFound() {
        Optional<Task> foundTask = taskRepository.findById(100L); // Non-existent ID
        assertTrue(foundTask.isEmpty());
        logger.info("Test findById_notFound passed.");
    }

    @Test
    public void testFindById_nullId() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.findById(null));
        logger.info("Test findById_nullId passed.");
    }

    @Test
    public void testFindAll_success() {
        em.persist(createTask("Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", LocalDateTime.now().plusDays(2), TaskStatus.COMPLETED));
        List<Task> tasks = taskRepository.findAll();
        assertEquals(2, tasks.size());
        logger.info("Test findAll_success passed.");
    }

    @Test
    public void testFindAll_empty() {
        List<Task> tasks = taskRepository.findAll();
        assertTrue(tasks.isEmpty());
        logger.info("Test findAll_empty passed.");
    }

    @Test
    public void testDelete_success() {
        Task task = createTask("Delete Task", "Delete Description", LocalDateTime.now().plusDays(3), TaskStatus.PENDING);
        em.persist(task);
        taskRepository.delete(task);
        Optional<Task> deletedTask = taskRepository.findById(task.getId());
        assertTrue(deletedTask.isEmpty());
        logger.info("Test delete_success passed.");
    }

    @Test
    public void testDelete_nullTask() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.delete(null));
        logger.info("Test delete_nullTask passed.");
    }

    @Test
    public void testDeleteById_success() {
        Task task = createTask("DeleteById Task", "DeleteById Description", LocalDateTime.now().plusDays(4), TaskStatus.PENDING);
        em.persist(task);
        taskRepository.deleteById(task.getId());
        Optional<Task> deletedTask = taskRepository.findById(task.getId());
        assertTrue(deletedTask.isEmpty());
        logger.info("Test deleteById_success passed.");
    }

    @Test
    public void testDeleteById_nonExistentId() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.deleteById(1000L)); // Non-existent ID
        logger.info("Test deleteById_nonExistentId passed.");
    }

    @Test
    public void testDeleteById_nullId() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.deleteById(null));
        logger.info("Test deleteById_nullId passed.");
    }

    @Test
    public void testFindByStatus_success() {
        em.persist(createTask("Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", LocalDateTime.now().plusDays(2), TaskStatus.COMPLETED));
        em.persist(createTask("Task 3", "Description 3", LocalDateTime.now().plusDays(3), TaskStatus.PENDING));
        List<Task> pendingTasks = taskRepository.findByStatus(TaskStatus.PENDING);
        assertEquals(2, pendingTasks.size());
        logger.info("Test findByStatus_success passed.");
    }

    @Test
    public void testFindByStatus_noMatchingTasks() {
        List<Task> cancelledTasks = taskRepository.findByStatus(TaskStatus.CANCELLED);
        assertTrue(cancelledTasks.isEmpty());
        logger.info("Test findByStatus_noMatchingTasks passed.");
    }

    @Test
    public void testFindByStatus_nullStatus() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.findByStatus(null));
        logger.info("Test findByStatus_nullStatus passed.");
    }

    @Test
    public void testFindByDueDateBefore_success() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(2), TaskStatus.COMPLETED));
        em.persist(createTask("Task 3", "Description 3", now.minusDays(1), TaskStatus.PENDING));
        List<Task> tasks = taskRepository.findByDueDateBefore(now);
        assertEquals(1, tasks.size());
        assertEquals("Task 3", tasks.get(0).getTitle());
        logger.info("Test findByDueDateBefore_success passed.");
    }

    @Test
    public void testFindByDueDateBefore_noMatchingTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByDueDateBefore(now.minusDays(1));
        assertTrue(tasks.isEmpty());
        logger.info("Test findByDueDateBefore_noMatchingTasks passed.");
    }

    @Test
    public void testFindByDueDateBefore_nullDate() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.findByDueDateBefore(null));
        logger.info("Test findByDueDateBefore_nullDate passed.");
    }

    @Test
    public void testFindByDueDateAfter_success() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(2), TaskStatus.COMPLETED));
        em.persist(createTask("Task 3", "Description 3", now.minusDays(1), TaskStatus.PENDING));
        List<Task> tasks = taskRepository.findByDueDateAfter(now);
        assertEquals(2, tasks.size());
        logger.info("Test findByDueDateAfter_success passed.");
    }

    @Test
    public void testFindByDueDateAfter_noMatchingTasks() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByDueDateAfter(now.plusDays(10));
        assertTrue(tasks.isEmpty());
        logger.info("Test findByDueDateAfter_noMatchingTasks passed.");
    }

    @Test
    public void testFindByDueDateAfter_nullDate() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.findByDueDateAfter(null));
        logger.info("Test findByDueDateAfter_nullDate passed.");
    }

    @Test
    public void testFindAllSortedByDueDate_ascending() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(2), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(1), TaskStatus.COMPLETED));
        em.persist(createTask("Task 3", "Description 3", now.plusDays(3), TaskStatus.PENDING));
        List<Task> tasks = taskRepository.findAllSortedByDueDate(true);
        assertEquals(3, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle()); // Earliest due date first
        assertEquals("Task 3", tasks.get(2).getTitle()); // Latest due date last
        logger.info("Test findAllSortedByDueDate_ascending passed.");
    }

    @Test
    public void testFindAllSortedByDueDate_descending() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(2), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(1), TaskStatus.COMPLETED));
        em.persist(createTask("Task 3", "Description 3", now.plusDays(3), TaskStatus.PENDING));
        List<Task> tasks = taskRepository.findAllSortedByDueDate(false);
        assertEquals(3, tasks.size());
        assertEquals("Task 3", tasks.get(0).getTitle()); // Latest due date first
        assertEquals("Task 2", tasks.get(2).getTitle()); // Earliest due date last
        logger.info("Test findAllSortedByDueDate_descending passed.");
    }

    @Test
    public void testFindByStatusAndSortByDueDate_ascending() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(2), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 3", "Description 3", now.plusDays(3), TaskStatus.COMPLETED));
        List<Task> tasks = taskRepository.findByStatusAndSortByDueDate(TaskStatus.PENDING, true);
        assertEquals(2, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle()); // Earliest due date first among PENDING
        assertEquals("Task 1", tasks.get(1).getTitle());
        logger.info("Test findByStatusAndSortByDueDate_ascending passed.");
    }

    @Test
    public void testFindByStatusAndSortByDueDate_descending() {
        LocalDateTime now = LocalDateTime.now();
        em.persist(createTask("Task 1", "Description 1", now.plusDays(2), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", now.plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 3", "Description 3", now.plusDays(3), TaskStatus.COMPLETED));
        List<Task> tasks = taskRepository.findByStatusAndSortByDueDate(TaskStatus.PENDING, false);
        assertEquals(2, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle()); // Latest due date first among PENDING
        assertEquals("Task 2", tasks.get(1).getTitle());
        logger.info("Test findByStatusAndSortByDueDate_descending passed.");
    }

    @Test
    public void testFindByStatusAndSortByDueDate_nullStatus() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.findByStatusAndSortByDueDate(null, true));
        logger.info("Test findByStatusAndSortByDueDate_nullStatus passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_titleMatch() {
        em.persist(createTask("Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", LocalDateTime.now().plusDays(2), TaskStatus.COMPLETED));
        List<Task> tasks = taskRepository.searchByTitleOrDescription("Task 1");
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        logger.info("Test searchByTitleOrDescription_titleMatch passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_descriptionMatch() {
        em.persist(createTask("Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.PENDING));
        em.persist(createTask("Task 2", "Description 2", LocalDateTime.now().plusDays(2), TaskStatus.COMPLETED));
        List<Task> tasks = taskRepository.searchByTitleOrDescription("Description 2");
        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle());
        logger.info("Test searchByTitleOrDescription_descriptionMatch passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_noMatch() {
        List<Task> tasks = taskRepository.searchByTitleOrDescription("NonExistent");
        assertTrue(tasks.isEmpty());
        logger.info("Test searchByTitleOrDescription_noMatch passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_caseInsensitive() {
        em.persist(createTask("Task 1", "Description 1", LocalDateTime.now().plusDays(1), TaskStatus.PENDING));
        List<Task> tasks = taskRepository.searchByTitleOrDescription("task 1"); // Lowercase search
        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        logger.info("Test searchByTitleOrDescription_caseInsensitive passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_nullKeyword() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.searchByTitleOrDescription(null));
        logger.info("Test searchByTitleOrDescription_nullKeyword passed.");
    }

    @Test
    public void testSearchByTitleOrDescription_emptyKeyword() {
        assertThrows(IllegalArgumentException.class, () -> taskRepository.searchByTitleOrDescription(""));
        logger.info("Test searchByTitleOrDescription_emptyKeyword passed.");
    }
}

