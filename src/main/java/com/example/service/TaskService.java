package com.example.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.exception.ObjectNotFoundException;
import com.example.model.Task;
import com.example.model.TaskState;
import com.example.model.User;
import com.example.repository.TaskRepository;
import com.example.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final EntityManager entityManager;

    @Transactional
    public void createTask(Task task) {
        taskRepository.save(task);
    }

    public Optional<Task> getTaskById(int id) {
        TypedQuery<Task> query = entityManager.createQuery("select t from Task t left join fetch t.owner o left join fetch t.executor e left join fetch t.commentList c WHERE t.id = ?1", Task.class);
        List<Task> taskList = query.setParameter(1, id).getResultList();
        if (taskList.isEmpty()) return Optional.empty();
        return Optional.ofNullable(taskList.get(0));
    }

    public List<Task> getTaskListByOwnerId(int ownerId) {
        Optional<User> ownerOptional = userRepository.findById(ownerId);
        if (ownerOptional.isEmpty()) throw new ObjectNotFoundException("User not found with by id" + ownerId);

        TypedQuery<Task> query = entityManager.createQuery("select t from Task t left join fetch t.owner o left join fetch t.executor e left join fetch t.commentList c WHERE o.id = ?1", Task.class);
        return query.setParameter(1, ownerId).getResultList();
    }

    public List<Task> getTaskListByOwnerId(int ownerId, int page, int tasksPerPage) {
        Optional<User> ownerOptional = userRepository.findById(ownerId);
        if (ownerOptional.isEmpty()) throw new ObjectNotFoundException("User not found with id" + ownerId);

        TypedQuery<Task> query = entityManager.createQuery("select t from Task t left join fetch t.owner o left join fetch t.executor e left join fetch t.commentList c WHERE o.id = ?1", Task.class);
        return query.setParameter(1, ownerId).setMaxResults(tasksPerPage).setFirstResult(page * tasksPerPage).getResultList();
    }

    public List<Task> getTaskListByExecutorId(int executorId) {
        Optional<User> executorOptional = userRepository.findById(executorId);
        if (executorOptional.isEmpty()) throw new ObjectNotFoundException("User not found with id" + executorId);


        TypedQuery<Task> query = entityManager.createQuery("select t from Task t left join fetch t.owner o left join fetch t.executor e left join fetch t.commentList c WHERE e.id = ?1", Task.class);
        return query.setParameter(1, executorId).getResultList();
    }

    public List<Task> getTaskListByExecutorId(int executorId, int page, int tasksPerPage) {
        Optional<User> executorOptional = userRepository.findById(executorId);
        if (executorOptional.isEmpty()) throw new ObjectNotFoundException("User not found");

        TypedQuery<Task> query = entityManager.createQuery("select t from Task t left join fetch t.owner o left join fetch t.executor e left join fetch t.commentList c WHERE e.id = ?1", Task.class);
        return query.setParameter(1, executorId).setMaxResults(tasksPerPage).setFirstResult(page * tasksPerPage).getResultList();
    }

    @Transactional
    public void deleteTask(int taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public void changeTaskStatus(int taskId, TaskState newTaskState) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) throw new ObjectNotFoundException("Task not found status");

        Task task = taskOptional.get();
        task.setTaskState(newTaskState);
        taskRepository.save(task);
    }

    @Transactional
    public void setExecutor(int taskId, int executorId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) throw new ObjectNotFoundException("Task not found");
        Optional<User> userOptional = userRepository.findById(executorId);
        if (userOptional.isEmpty()) throw new ObjectNotFoundException("User not found");

        Task task = taskOptional.get();
        User executor = userOptional.get();
        task.setExecutor(executor);
        taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(int taskId, Task newTask) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isEmpty()) throw new ObjectNotFoundException("Task not found update");

        Task task = taskOptional.get();
        task.setTitle(newTask.getTitle());
        task.setDescription(newTask.getDescription());
        task.setTaskPriority(newTask.getTaskPriority());
        task.setTaskState(newTask.getTaskState());
        task.setOwner(newTask.getOwner());
        task.setExecutor(newTask.getExecutor());
        return task;
    }
}