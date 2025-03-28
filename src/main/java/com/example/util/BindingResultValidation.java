package com.example.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import com.example.exception.ValidationFailException;
import java.util.List;

public class BindingResultValidation {

    public static void bindingResultCheck(BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors)
                errorMessage.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            throw new ValidationFailException(errorMessage.toString());
        }
    }
}
