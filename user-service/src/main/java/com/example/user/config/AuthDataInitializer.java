package com.example.user.config;

import com.example.user.model.Permission;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.repository.PermissionRepository;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.PasswordService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AuthDataInitializer implements CommandLineRunner {

    private static final String ADMIN_SALT = "demo-static-salt";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordService passwordService;

    public AuthDataInitializer(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordService passwordService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordService = passwordService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Permission usersRead = permission("users:read");
        Permission usersWrite = permission("users:write");
        Permission studentsRead = permission("students:read");

        Role admin = role("ADMIN", List.of(usersRead, usersWrite, studentsRead));
        role("USER", List.of(usersRead, studentsRead));

        userRepository.findByUsername("admin")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("admin");
                    user.setName("Admin User");
                    user.setEmail("admin@example.com");
                    user.setEnabled(true);
                    user.setPasswordSalt(ADMIN_SALT);
                    user.setPasswordHash(passwordService.hash("admin", ADMIN_SALT));
                    user.getRoles().add(admin);
                    return userRepository.save(user);
                });
    }

    private Permission permission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return permissionRepository.save(permission);
                });
    }

    private Role role(String name, List<Permission> permissions) {
        Role role = roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role nextRole = new Role();
                    nextRole.setName(name);
                    return roleRepository.save(nextRole);
                });
        role.getPermissions().addAll(permissions);
        return roleRepository.save(role);
    }
}
