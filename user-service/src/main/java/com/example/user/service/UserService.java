package com.example.user.service;

import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.common.utils.PasswordService;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            setPassword(user, user.getPassword());
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (userDetails.getUsername() != null) {
                user.setUsername(userDetails.getUsername());
            }
            if (userDetails.getName() != null) {
                user.setName(userDetails.getName());
            }
            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getEnabled() != null) {
                user.setEnabled(userDetails.getEnabled());
            }
            if (userDetails.getPassword() != null && !userDetails.getPassword().isBlank()) {
                setPassword(user, userDetails.getPassword());
            }
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private void setPassword(User user, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        String salt = UUID.randomUUID().toString();
        user.setPasswordSalt(salt);
        user.setPasswordHash(passwordService.hash(rawPassword));
    }
}
