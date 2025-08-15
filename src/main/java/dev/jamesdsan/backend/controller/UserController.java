package dev.jamesdsan.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.jamesdsan.backend.dto.UserResponse;
import dev.jamesdsan.backend.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }
}
