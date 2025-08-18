package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.UserResponse;
import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.utils.Constants.Providers;
import dev.jamesdsan.utils.Constants.Roles;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> listUsers() {
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

    public UserResponse getUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void createUser(User user) {
        if (user == null || Strings.isBlank(user.getUsername()) || Strings.isBlank(user.getPassword())
                || Strings.isBlank(user.getEmail())) {
            throw new ValidationException("User could not be null");
        }

        if (Strings.isBlank(user.getRole())) {
            user.setRole(Roles.USER);
        }

        if (Strings.isBlank(user.getProvider())) {
            user.setProvider(Providers.LOCAL);
        }

        userRepository.save(user);
    }

    public void updateUser(long userId, User user) {
        if (user == null || Strings.isBlank(user.getUsername()) || Strings.isBlank(user.getPassword())
                || Strings.isBlank(user.getEmail())) {
            throw new ValidationException("User could not be updated");
        }
        user.setId(userId);
        userRepository.save(user);
    }

    public void deleteUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException());
        userRepository.delete(user);
    }
}
