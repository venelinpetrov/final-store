package com.vpe.finalstore.config;

import com.vpe.finalstore.common.dtos.ErrorDto;
import com.vpe.finalstore.exceptions.ApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles custom API exceptions (business logic errors)
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDto> handleApiException(ApiException ex) {
        ErrorDto errorDto = new ErrorDto(ex.getMessage(), null);
        return ResponseEntity.status(ex.getStatus()).body(errorDto);
    }

    // Handles validation errors thrown by @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.toList());

        ErrorDto errorDto = new ErrorDto("Validation failed", errors);
        return ResponseEntity.badRequest().body(errorDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorDto> handleDataIntegrityViolation() {
        var message = "Operation failed due to related data. This record is used elsewhere and cannot be deleted";
        var errorDto = new ErrorDto(message, null);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDto);
    }

    // Catches all other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGeneralException(Exception ex) {
        // Log the full exception stack trace for internal debugging
        ex.printStackTrace(); // TODO Logger service / Sentry
        ErrorDto errorDto = new ErrorDto("An unexpected error occurred", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }
}
