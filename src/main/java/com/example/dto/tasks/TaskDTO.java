package com.example.dto.tasks;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.example.model.TaskPriority;
import com.example.model.TaskState;

@Setter
@Getter
public class TaskDTO {

    @NotNull(message = "field is required")
    @Schema(name = "title", example = "task-1", required = true)
    private String title;

    @Schema(name = "description", example = "some task description")
    private String description;

    @NotNull(message = "task state")
    @Schema(name = "taskState", example = "IN_PROGRESS", required = true)
    private TaskState taskState;

    @NotNull(message = "field is required")
    @Schema(name = "taskPriority", example = "HI", required = true)
    private TaskPriority taskPriority;

    @NotNull(message = "field is required")
    @Schema(name = "ownerId", example = "1", required = true)
    private Integer ownerId;

    @Schema(name = "executorId", example = "1")
    private Integer executorId;

    public TaskDTO() {
    }
}
