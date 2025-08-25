package dev.jamesdsan.backend.config;

import dev.jamesdsan.backend.handler.CustomAuthenticationSuccessHandler;
import dev.jamesdsan.backend.service.CustomOidcUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	private CustomOidcUserService customOAuth2UserService;

	@Autowired
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		logger.info("[SecurityConfig] initializing securityFilterChain");

		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/error", "/login").permitAll()
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.oidcUserService(customOAuth2UserService))
						.successHandler(customAuthenticationSuccessHandler))
				.httpBasic(Customizer.withDefaults());
		return http.build();
	}
}