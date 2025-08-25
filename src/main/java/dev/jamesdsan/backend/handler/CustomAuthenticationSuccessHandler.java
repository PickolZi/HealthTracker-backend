package dev.jamesdsan.backend.handler;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    CustomAuthenticationSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("[CustomAuthenticationSuccessHandler] starting check for successful OAuth custom authentication");

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        logger.info("[CustomAuthenticationSuccessHandler] Oidc user: {}", oidcUser);

        User user = userRepository.findByEmail(oidcUser.getEmail());
        logger.info("[CustomAuthenticationSuccessHandler] user: {}", user);

        String jwt = jwtUtil.generateToken("" + user.getId(), user.getEmail());

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);

        logger.info("[CustomAuthenticationSuccessHandler] end of check for OAuth custom authentication");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}