package dev.jamesdsan.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.utils.Constants.Providers;
import dev.jamesdsan.utils.Constants.Roles;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oAuth2User = super.loadUser(userRequest);

        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            // Will run if the user is signing up for the first time through Google
            user = new User();
            user.setProvider(Providers.GOOGLE);
            user.setProviderId(providerId);
            user.setRole(Roles.USER);
        }

        // Update these values in case they updated it through their google profile
        user.setEmail(email);
        user.setUsername(name);

        userRepository.save(user);

        return oAuth2User;
    }
}
