package com.example.dto.comments;

import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;
import com.example.exception.ObjectNotFoundException;
import com.example.model.Comment;
import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.UserService;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentDTOToCommentConverter implements Converter<CommentDTO, Comment> {
    private final UserService userService;
    private final TaskService taskService;


    @Override
    public Comment convert(MappingContext<CommentDTO, Comment> mappingContext) {
        Comment comment = mappingContext.getDestination();
        CommentDTO commentDTO = mappingContext.getSource();
        if (comment == null) {
            comment = new Comment();
        }
        comment.setText(commentDTO.getText());


        if (commentDTO.getTaskId() != null) {
            Optional<Task> taskOptional = taskService.getTaskById(commentDTO.getTaskId());
            if (taskOptional.isEmpty()) throw new ObjectNotFoundException("Task not found");
            Task task = taskOptional.get();
            comment.setTask(task);
        } else comment.setTask(null);

        return comment;
    }
}
