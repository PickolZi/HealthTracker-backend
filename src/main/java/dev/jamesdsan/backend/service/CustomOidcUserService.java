package dev.jamesdsan.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.utils.Constants.Providers;
import dev.jamesdsan.backend.utils.Constants.Roles;

@Service
public class CustomOidcUserService extends OidcUserService {
    private static final Logger logger = LoggerFactory.getLogger(CustomOidcUserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("[CustomOidcUserService] user has signed in through OAuth");
        // This method is ran when the user finishes OAuth authentication and is
        // redirected back to /login/oauth2/code/google.
        validateGoogleToken(userRequest);

        OidcUser oAuth2User = super.loadUser(userRequest);

        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        logger.info("[CustomOidcUserService] user with email: {} is signing in through OAuth", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.info(
                    "[CustomOidcUserService] user is registering through google for the first time");
            user = new User();
            user.setProvider(Providers.GOOGLE);
            user.setProviderId(providerId);
            user.setRole(Roles.USER);
        }

        // Update these values in case they updated it through their google profile
        user.setEmail(email);
        user.setUsername(name);

        try {
            userRepository.save(user);
        } catch (Exception exc) {
            logger.error("[CustomOidcUserService] failed to login through OAuth. Not saving/updating user in database");
            throw exc;
        }

        logger.info("[CustomOidcUserService] user register/login with oauth successfully saved/updated to database");
        return oAuth2User;
    }

    private void validateGoogleToken(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            String tokenValue = userRequest.getIdToken().getTokenValue();
            authService.verifyGoogleToken(tokenValue);
        } catch (Exception exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("400"), exception.getMessage());
        }
    }
}
