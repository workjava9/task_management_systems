package com.example.dto.tasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import com.example.dto.comments.CommentDTOResponse;
import com.example.model.Task;
import com.example.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
@RequiredArgsConstructor
public class TaskToTaskDTOResponseConverter implements Converter<Task, TaskDTOResponse> {

    private final UserService userService;

    @Override
    public TaskDTOResponse convert(MappingContext<Task, TaskDTOResponse> mappingContext) {
        Task task = mappingContext.getSource();
        TaskDTOResponse taskDTO = mappingContext.getDestination();
        if (taskDTO == null) {
            taskDTO = new TaskDTOResponse();
        }
        taskDTO.setTitle(task.getTitle());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setTaskState(task.getTaskState());
        taskDTO.setTaskPriority(task.getTaskPriority());
        if (task.getOwner() != null) {
            taskDTO.setOwnerId(task.getOwner().getId());
        } else taskDTO.setOwnerId(null);

        if (task.getExecutor() != null) {
            taskDTO.setExecutorId(task.getExecutor().getId());
        } else taskDTO.setExecutorId(null);

        CommentDTOResponse commentDTOResponse = new CommentDTOResponse();
        List<CommentDTOResponse> commentDTOResponseList = task.getCommentList().stream().map(x -> {
            commentDTOResponse.setId(x.getId());
            commentDTOResponse.setText(x.getText());
            commentDTOResponse.setCommentOwnerId(x.getCommentOwner().getId());
            return commentDTOResponse;
        }).collect(Collectors.toList());
        taskDTO.setCommentList(commentDTOResponseList);

        return taskDTO;
    }

}
