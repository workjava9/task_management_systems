package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    @NotNull(message = "field is required")
    @Schema(name = "username", example = "user-1", required = true)
    private String username;

    @NotNull(message = "field is required")
    @Schema(name = "mail", example = "examle@mail.com", required = true)
    private String mail;

    @NotNull(message = "field is required")
    @Schema(name = "password", example = "P@ssw0rd", required = true)
    private String password;

}
