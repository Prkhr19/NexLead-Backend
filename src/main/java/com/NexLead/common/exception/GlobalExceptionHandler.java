package com.NexLead.common.exception;

import com.NexLead.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse response = new ErrorResponse();

        response.setSuccess(false);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage(ex.getMessage());

        Map<String, String>errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);

    }

    @ExceptionHandler(ResourceNotFoundException.class)
     public ResponseEntity<ErrorResponse>handleResourceNotFound(ResourceNotFoundException ex){
        ErrorResponse response = new ErrorResponse();
        response.setSuccess(false);
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(ex.getMessage());

        Map<String, String>errors = new HashMap<>();
        errors.put("error", ex.getMessage());

        response.setErrors(errors);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse>handleValidatonFailed(MethodArgumentNotValidException ex){
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Validation Failed");
        response.setSuccess(false);
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()){
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
