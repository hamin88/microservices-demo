package com.tpe.dto;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public record
TaskRequest(String jobId , String jobType, Long ruleId, String executionTime) {
    public Date getQuartzExecutionDate() {
        if (executionTime == null || executionTime.isBlank()) {
            throw new IllegalArgumentException("Execution time string cannot be null or empty");
        }

        // Safely parses ISO-8601 strings like "2026-08-25T15:30:00"
        LocalDateTime localDateTime = LocalDateTime.parse(executionTime, DateTimeFormatter.ISO_DATE_TIME);

        // Formats cleanly to legacy Date using the system's timezone
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}