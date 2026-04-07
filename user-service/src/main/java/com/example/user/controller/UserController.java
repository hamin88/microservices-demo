package com.example.user.controller;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping
    public List<String> getAllUsers() {
        return Arrays.asList("Admin_User", "Staff_User", "Guest_User");
    }
}
