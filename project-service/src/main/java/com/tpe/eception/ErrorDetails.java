package com.tpe.eception;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorDetails
        (LocalDateTime timestamp, String message, String details, Map<String, String> validationErrors) {

    public ErrorDetails(LocalDateTime timestamp, String message, String details ) {
        // It MUST call the canonical constructor using 'this'
        this( timestamp, message, details,null) ;
    }
}
