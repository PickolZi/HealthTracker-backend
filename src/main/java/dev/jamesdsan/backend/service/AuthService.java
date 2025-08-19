package dev.jamesdsan.backend.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final GoogleIdTokenVerifier verifier;

    public AuthService(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    public GoogleIdToken.Payload verifyGoogleToken(String idTokenString) throws Exception {
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        }
        throw new IllegalArgumentException("Invalid ID token");
    }
}
