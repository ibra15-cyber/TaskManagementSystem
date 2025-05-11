package com.ibra.taskmanager.repository;



import com.ibra.taskmanager.entity.Task;
import com.ibra.taskmanager.enums.TaskStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TaskRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Task save(Task task) {
        getCurrentSession().saveOrUpdate(task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        Task task = getCurrentSession().get(Task.class, id);
        return Optional.ofNullable(task);
    }

    @Override
    public List<Task> findAll() {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);
        cq.select(root);
        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public void delete(Task task) {
        getCurrentSession().delete(task);
    }

    @Override
    public void deleteById(Long id) {
        Task task = getCurrentSession().get(Task.class, id);
        if (task != null) {
            getCurrentSession().delete(task);
        }
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        cq.select(root)
                .where(cb.equal(root.get("status"), status));

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Task> findByDueDateBefore(LocalDateTime date) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        cq.select(root)
                .where(cb.lessThan(root.get("dueDate"), date));

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Task> findByDueDateAfter(LocalDateTime date) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        cq.select(root)
                .where(cb.greaterThan(root.get("dueDate"), date));

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Task> findAllSortedByDueDate(boolean ascending) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        cq.select(root);

        if (ascending) {
            cq.orderBy(cb.asc(root.get("dueDate")));
        } else {
            cq.orderBy(cb.desc(root.get("dueDate")));
        }

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Task> findByStatusAndSortByDueDate(TaskStatus status, boolean ascending) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        cq.select(root)
                .where(cb.equal(root.get("status"), status));

        if (ascending) {
            cq.orderBy(cb.asc(root.get("dueDate")));
        } else {
            cq.orderBy(cb.desc(root.get("dueDate")));
        }

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<Task> searchByTitleOrDescription(String keyword) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<Task> cq = cb.createQuery(Task.class);
        Root<Task> root = cq.from(Task.class);

        String likePattern = "%" + keyword + "%";

        Predicate titlePredicate = cb.like(cb.lower(root.get("title")), likePattern.toLowerCase());
        Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), likePattern.toLowerCase());

        cq.select(root)
                .where(cb.or(titlePredicate, descriptionPredicate));

        return getCurrentSession().createQuery(cq).getResultList();
    }
}