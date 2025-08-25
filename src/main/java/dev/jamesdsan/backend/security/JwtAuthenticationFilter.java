package dev.jamesdsan.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.utils.JwtUtil;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        logger.info("[JwtAuthenticationFilter] starting check for jwt");

        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.info("[JwtAuthenticationFilter] found a valid jwt: {}", jwt);
                }
            }
        }

        if (jwt != null && jwtUtil.isTokenValid(jwt)) {
            String userId = jwtUtil.validateTokenAndGetUserId(jwt);
            User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
            if (user != null) {
                logger.info(
                        "[JwtAuthenticationFilter] User with id: {} and name: {} is valid from jwt", user.getId(),
                        user.getUsername());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null);
                // SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        logger.info("[JwtAuthenticationFilter] end of check for jwt");
        filterChain.doFilter(request, response);
    }
}
