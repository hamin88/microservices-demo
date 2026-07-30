package com.example.student.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Value("${server.port}")
    private String port;

    @RateLimiter(name = "getAllStudents", fallbackMethod = "fallback")
    @GetMapping("/getAllStudents")
    public List<String> getAllStudents() {
        log.info ("welecome to student service");
        return Arrays.asList("Alice Johnson (Port: " + port + ")", "Bob Smith", "Charlie Brown");
    }

    public List<String>  fallback(RequestNotPermitted ex) {
        log.info ("welecome to fullback method");
        return  Arrays.asList("Too many requests. Try again later." );
    }
}
