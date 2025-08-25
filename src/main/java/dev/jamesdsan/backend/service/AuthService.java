package dev.jamesdsan.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final GoogleIdTokenVerifier verifier;

    public AuthService(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        logger.info("[AuthService] validating OAuth google token");
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            logger.info("[AuthService] successfully validated OAuth google token");
            return idToken.getPayload();
        }
        logger.error("[AuthService] failed to validate OAuth google token. Invalid ID token");
        throw new IllegalArgumentException("Invalid ID token");
    }
}
