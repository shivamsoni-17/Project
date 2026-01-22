package com.hmsService.service;

import com.hmsService.dto.UpdateProfileRequest;
import com.hmsService.model.User;
import com.hmsService.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(null);
        return user;
    }

    @Transactional
    public User updateProfile(Long id, UpdateProfileRequest request) {
        User existing = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            existing.setFullName(request.getFullName());
        }
        if (request.getMobile() != null && !request.getMobile().isBlank()) {
            existing.setMobile(request.getMobile());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.update(existing);
        existing.setPassword(null);
        return existing;
    }

    public List<User> list(String query) {
        List<User> users = userRepository.search(query);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    public User updateRole(Long id, String role) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(role);
        userRepository.update(user);
        user.setPassword(null);
        return user;
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if ("admin".equals(user.getRole())) {
            throw new IllegalArgumentException("Cannot delete admin users");
        }
        userRepository.delete(id);
    }
}

