package com.example.validation;

import com.example.dto.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import com.example.service.UserService;

@Component
public class RegistrationValidation implements Validator {
    private final UserService userService;

    public RegistrationValidation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserDTO userDTO = (UserDTO) target;
        if (userService.getUserByUsername(userDTO.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "This username already exist");
        }
        if (userService.getUserByMail(userDTO.getMail()).isPresent()) {
            errors.rejectValue("mail", "", "This mail already exist");
        }
    }
}
