package dev.jamesdsan.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.UserResponse;
import dev.jamesdsan.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .toList();
    }
}
