package dev.jamesdsan.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import dev.jamesdsan.backend.security.UserPrincipal;

@Component
public class AuthenticatedUserService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticatedUserService.class);

    public UserPrincipal getCurrentUser() {
        logger.info("[AuthenticatedUserService] fetching current user from SecurityContextHolder");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error(
                    "[AuthenticatedUserService] failed to find an authenticated user in the SecurityContextHolder");
            throw new IllegalStateException("No authenticated user found");
        }

        Object userPrincipal = authentication.getPrincipal();
        if (!(userPrincipal instanceof UserPrincipal)) {
            logger.error(
                    "[AuthenticatedUserService] failed to return authenticated userPrincipal because of unsupported authentication type: {}",
                    userPrincipal.getClass().getName());
            throw new AuthenticationCredentialsNotFoundException(
                    "Unsupported authentication type");
        }

        return (UserPrincipal) userPrincipal;
    }

    public void isUserAuthorizedElseThrowAccessDeniedException(long userId) {
        UserPrincipal userPrincipal = this.getCurrentUser();
        if (userPrincipal.getId() != userId) {
            logger.error(
                    "[AuthenticatedUserService] failed to access user with id: {} because current user with id: {} does not have access",
                    userId,
                    userPrincipal.getId());

            throw new AccessDeniedException(
                    String.format(
                            "Current user: %d does not have access to user: %d",
                            userPrincipal.getId(),
                            userId));
        }

        logger.info(
                "[AuthenticatedUserService] successfully authorized to get user info with id: {} through current user with id: {}",
                userId,
                userPrincipal.getId());
    }
}
