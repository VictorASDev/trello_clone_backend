package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.record.*;
import com.victor.trello_clone.service.AuthService;
import com.victor.trello_clone.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        authService.signUp(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<Void> signIn(
            @RequestBody AuthRequest request,
            HttpServletResponse response) throws CredentialException {

        var tokens = authService.generateTokens(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(false) //TODO: trocar para true em prod
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofSeconds(tokens.refreshExpiresAt()))
                .sameSite("Strict")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.accessToken())
                .httpOnly(true)
                .secure(false) //TODO: trocar para true em prod
                .path("/api/v1")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refreshToken(
            @CookieValue(value = "refresh_token") String refreshToken,
            HttpServletResponse response) {

        var accessToken = authService.refreshToken(refreshToken);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken.accessToken())
                .httpOnly(true)
                .secure(false) //TODO: trocar para true em prod
                .path("/api/v1")
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());


        return ResponseEntity.noContent().build();
    }

    @PostMapping("/send/token")
    public ResponseEntity<?> sendVerification(@RequestBody ValidateEmailRequest req) {

        authService.sendVerificationEmail(req.userEmail());

        return ResponseEntity.ok(
                Map.of("message",
                        "Se o email existir, enviaremos instruções."));
    }


    @PatchMapping("/send/validation")
    public ResponseEntity<Void> validateEmailToken(@RequestBody TokenRequest request) {
        authService.validateEmail(request.token());

        return ResponseEntity.ok().build();
    }
}
