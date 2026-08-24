package com.globetrotter.service;

import com.globetrotter.dto.UserResponse;
import com.globetrotter.entity.User;
import com.globetrotter.exception.ResourceNotFoundException;
import com.globetrotter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponseByEmail(String email) {
        User user = getUserByEmail(email);
        return UserResponse.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
