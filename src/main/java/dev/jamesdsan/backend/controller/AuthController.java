package dev.jamesdsan.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import dev.jamesdsan.backend.service.AuthService;
import dev.jamesdsan.backend.service.UserService;
import dev.jamesdsan.backend.utils.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = { "*" })
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private final AuthService authService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final JwtUtil jwtUtil;

    @PostMapping("/google")
    public ResponseEntity<ResponseOutput> getCustomJwtUsingGoogleId(@RequestBody RequestInput requestInput) {
        logger.info("[AuthController] creating custom JWT from frontend's Google Token");

        String jwtToken = "";
        try {
            GoogleIdToken.Payload payload = authService.verifyGoogleToken(requestInput.googleIdToken);

            String userId = Long.toString(userService.getUserIdByEmail(payload.getEmail()));

            jwtToken = jwtUtil.generateToken(userId, payload.getEmail());

        } catch (Exception exc) {
            logger.error("[AuthController] failed to create custom JWT");
            return ResponseEntity.badRequest().build();
        }

        logger.info("[AuthController] successfully created custom JWT");
        return ResponseEntity.ok(new ResponseOutput(jwtToken));
    }
}

final class RequestInput {
    public String googleIdToken;
}

@AllArgsConstructor
final class ResponseOutput {
    public String jwtToken;
}