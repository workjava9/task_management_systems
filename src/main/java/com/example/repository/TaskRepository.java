package com.example.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Task;
import com.example.model.User;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> getTasksByOwner(User owner);

    List<Task> getTasksByOwner(User owner, Pageable pageable);

    List<Task> getTasksByExecutor(User executor);

    List<Task> getTasksByExecutor(User executor, Pageable pageable);

}
