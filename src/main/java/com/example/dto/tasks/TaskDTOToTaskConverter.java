package com.example.dto.tasks;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import com.example.exception.ObjectNotFoundException;
import com.example.model.Task;
import com.example.model.User;
import com.example.service.UserService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskDTOToTaskConverter implements Converter<TaskDTO, Task> {

    private final UserService userService;

    @Override
    public Task convert(MappingContext<TaskDTO, Task> mappingContext) {
        Task task = mappingContext.getDestination();
        TaskDTO taskDTO = mappingContext.getSource();
        if (task == null) {
            task = new Task();
        }
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setTaskState(taskDTO.getTaskState());
        task.setTaskPriority(taskDTO.getTaskPriority());
        if (taskDTO.getOwnerId() != null) {
            Optional<User> ownerOptional = userService.getUserById(taskDTO.getOwnerId());
            if (ownerOptional.isEmpty()) throw new ObjectNotFoundException("User not found");
            User owner = ownerOptional.get();
            task.setOwner(owner);
        } else task.setOwner(null);

        if (taskDTO.getExecutorId() != null) {
            Optional<User> executorOptional = userService.getUserById(taskDTO.getExecutorId());
            if (executorOptional.isEmpty()) throw new ObjectNotFoundException("User not found");
            User executor = executorOptional.get();
            task.setExecutor(executor);
        } else task.setExecutor(null);

        return task;
    }
}
