package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.record.AccessTokenResponse;
import com.victor.trello_clone.data.record.AuthRequest;
import com.victor.trello_clone.data.record.SignUpRequest;
import com.victor.trello_clone.service.AuthService;
import com.victor.trello_clone.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.CredentialException;
import java.time.Duration;

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
        userService.create(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<AccessTokenResponse> signIn(
            @RequestBody AuthRequest request,
            HttpServletResponse response) throws CredentialException {

        var tokens = authService.generateTokens(request);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(false) //trocar pra true em prod
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());



        return ResponseEntity.ok(
                new AccessTokenResponse(tokens.accessToken(), tokens.accessExpiresIn())
        );
    }

    @GetMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @CookieValue(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
