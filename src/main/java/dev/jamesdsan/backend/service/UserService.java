package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.UserResponse;
import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.exception.UserNotFoundException;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.utils.Constants.Providers;
import dev.jamesdsan.backend.utils.Constants.Roles;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final AuthenticatedUserService authenticatedUserService;

    public List<UserResponse> listUsers() {
        logger.info("[UserService] fetching list of users");

        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .toList();

        logger.info("[UserService] successfully retrieved list of {} users: {}", users.size(), users);

        return users;
    }

    public UserResponse getUser(long userId) {
        logger.info("[UserService] fetching user with id: {}", userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdOrThrowUserNotFoundException(userId);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        logger.info("[UserService] successfully found user: {}", userResponse);

        return userResponse;
    }

    public void createUser(User user) {
        logger.info("[UserService] creating user with username: {}", user.getUsername());

        if (user == null || Strings.isBlank(user.getUsername()) || Strings.isBlank(user.getPassword())
                || Strings.isBlank(user.getEmail())) {
            logger.error("[UserService] failed to create user because user can not be null");
            throw new ValidationException("User request can not be null");
        }

        if (Strings.isBlank(user.getRole())) {
            user.setRole(Roles.USER);
        }

        if (Strings.isBlank(user.getProvider())) {
            user.setProvider(Providers.LOCAL);
        }

        validateUniqueUsernameAndEmailElseThrowValidationException(user);

        try {
            User createdUser = userRepository.save(user);
            logger.info("[UserService] successfully created user with id: {} and username: {}",
                    createdUser.getId(),
                    createdUser.getUsername());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[UserService] failed to create user because of invalid user passed: {}", exc.getMessage());
            throw new ValidationException("This user already exists, try logging in using the username of email");
        } catch (Exception exc) {
            logger.error("[UserService] failed to create user because of unknown exception: ", exc.getMessage());
            throw new ValidationException("Bad user request was passed");
        }
    }

    public void updateUser(long userId, User user) {
        logger.info("[UserService] updating user with id: {}", userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User curUser = findUserByIdOrThrowUserNotFoundException(userId);

        if (user == null) {
            logger.error("[UserService] failed to update user because user input can not be null");
            throw new ValidationException("User request can not be null");
        }

        validateUniqueUsernameAndEmailElseThrowValidationException(curUser, user);

        try {
            User mergedUser = curUser.merge(user);
            User updatedUser = userRepository.save(mergedUser);
            logger.info("[UserService] successfully updated user with id: {} and username: {}", updatedUser.getId(),
                    updatedUser.getUsername());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[UserService] failed to update user because of invalid user passed: {}", exc.getMessage());
            throw new ValidationException("This user already exists, try another username or email");
        } catch (Exception exc) {
            logger.error("[UserService] failed to update user because of unknown exception: ", exc.getMessage());
            throw new ValidationException("Bad user request was passed");
        }
    }

    public void deleteUser(long userId) {
        logger.info("[UserService] attempting to delete user with id: {}", userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdOrThrowUserNotFoundException(userId);

        try {
            userRepository.delete(user);
            logger.info("[UserService] successfully deleted user with id: {}", user.getId());
        } catch (Exception exc) {
            logger.error("[UserService] failed to delete user with id: {}", user.getId());
            throw exc;
        }
    }

    private User findUserByIdOrThrowUserNotFoundException(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.error("[UserService] failed to find user with id: {}", userId);
            throw new UserNotFoundException(userId);
        }

        logger.info("[UserService] successfully found user with id: {}", userId);
        return user;
    }

    private void validateUniqueUsernameAndEmailElseThrowValidationException(User user) {
        // When creating user - Username and Email has to be unique within db
        if (user == null)
            return;

        if (Strings.isNotBlank(user.getUsername()) && userRepository.findByUsername(user.getUsername()) != null) {
            logger.error("[UserService] failed to validate user. Username already exists");
            throw new ValidationException("Bad request, this username is already taken");
        }

        if (Strings.isNotBlank(user.getEmail()) && userRepository.findByEmail(user.getEmail()) != null) {
            logger.error("[UserService] failed to validate user. Email already exists");
            throw new ValidationException("Bad request, this email is already taken");
        }
    }

    private void validateUniqueUsernameAndEmailElseThrowValidationException(User curUser, User newUser) {
        // When updating user - Username and Email has to be unique unless newUser
        // username or email remains the same
        if (curUser == null || newUser == null)
            return;

        if (Strings.isNotBlank(newUser.getUsername()) &&
                !curUser.getUsername().equals(newUser.getUsername()) &&
                userRepository.findByUsername(newUser.getUsername()) != null) {
            logger.error("[UserService] failed to validate user. Username already exists");
            throw new ValidationException("Bad request, this username is already taken");
        }

        if (Strings.isNotBlank(newUser.getEmail()) &&
                !curUser.getEmail().equals(newUser.getEmail()) &&
                userRepository.findByEmail(newUser.getEmail()) != null) {
            logger.error("[UserService] failed to validate user. Email already exists");
            throw new ValidationException("Bad request, this email is already taken");
        }
    }
}
