package com.tpe.service;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class JobTaskFactory {

    // Define your various business logic tasks as Runnables
    private final Map<String, Runnable> taskMap = Map.of(
        "EMAIL", () -> System.out.println("Executing EMAIL job at " + System.currentTimeMillis()),
        "CLEANUP", () -> System.out.println("Executing DB CLEANUP job at " + System.currentTimeMillis()),
        "REPORT", () -> System.out.println("Executing REPORT generation job at " + System.currentTimeMillis())
    );

    public Runnable getTask(String jobType) {
        Runnable task = taskMap.get(jobType.toUpperCase());
        if (task == null) {
            throw new IllegalArgumentException("Unknown job type: " + jobType);
        }
        return task;
    }
}
