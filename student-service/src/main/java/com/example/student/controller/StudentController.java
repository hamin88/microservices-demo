package com.example.student.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Value("${server.port}")
    private String port;

    @GetMapping
    public List<String> getAllStudents() {
        return Arrays.asList("Alice Johnson (Port: " + port + ")", "Bob Smith", "Charlie Brown");
    }
}
