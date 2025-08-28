package dev.jamesdsan.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.utils.JwtUtil;

import java.io.IOException;
import java.util.List;

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

        logger.info("[JwtAuthenticationFilter] checking securityContextHolder {}", SecurityContextHolder.getContext());

        String jwt = fetchJwtTokenElseNull(request);

        if (jwt != null && jwtUtil.isTokenValid(jwt)) {
            setSecurityContextHolderAuthentication(jwt);
        } else {
            logger.error("[JwtAuthenticationFilter] failed to find or validate jwt cookie: {}", jwt);
        }

        logger.info("[JwtAuthenticationFilter] end of check for jwt");
        filterChain.doFilter(request, response);
    }

    private String fetchJwtTokenElseNull(HttpServletRequest request) {
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.info("[JwtAuthenticationFilter] found a valid jwt: {}", jwt);
                }
            }
        }
        return jwt;
    }

    private void setSecurityContextHolderAuthentication(String jwt) {
        String userId = jwtUtil.validateTokenAndGetUserId(jwt);
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);

        if (user != null) {
            logger.info(
                    "[JwtAuthenticationFilter] User with id: {} and name: {} is valid from jwt", user.getId(),
                    user.getUsername());

            UserPrincipal userPrincipal = new UserPrincipal(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    List.of(user.getRole()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null, userPrincipal.getAuthorities());
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            logger.info(
                    "[JwtAuthenticationFilter] Setting authentication in SecurityContextHolder to {}",
                    authentication);
        }
    }
}
