package dev.jamesdsan.backend.config;

import dev.jamesdsan.backend.handler.CustomAuthenticationSuccessHandler;
import dev.jamesdsan.backend.service.CustomOidcUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Autowired
	private CustomOidcUserService customOAuth2UserService;

	@Autowired
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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