package com.example.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTO {

    @NotNull(message = "field is required")
    @Schema(name = "text", example = "new comment for task", required = true)
    private String text;

    @NotNull(message = "field is required")
    @Schema(name = "taskId", example = "1", required = true)
    private Integer taskId;

    public CommentDTO() {
    }

}
