package com.tpe.eception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Option 1 : @ResponseStatus(HttpStatus.NOT_FOUND)
// Option 2: Use GlobalExceptionHandler to handle the exception and return a custom error response
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
