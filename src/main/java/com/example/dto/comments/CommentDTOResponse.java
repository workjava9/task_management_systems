package com.example.dto.comments;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDTOResponse {
    @Schema(name = "id", example = "1")
    private int id;

    @Schema(name = "text", example = "new comment for task")
    private String text;

    @Schema(name = "commentOwnerId", example = "1")
    private Integer commentOwnerId;

    public CommentDTOResponse() {
    }

}
