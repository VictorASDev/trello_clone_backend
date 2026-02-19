package com.victor.trello_clone.service;

import com.victor.trello_clone.model.User;
import com.victor.trello_clone.data.record.AccessTokenResponse;
import com.victor.trello_clone.repository.UserRepository;
import com.victor.trello_clone.data.record.AuthRequest;
import com.victor.trello_clone.data.record.TokenResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.security.auth.login.CredentialException;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private static final long ACCESS_EXPIRES_IN = 15 * 60;
    private static final long REFRESH_EXPIRES_IN = 7 * 24 * 3600;
    private static final String ISSUER = "trello-clone-api";

    private  final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       JwtDecoder jwtDecoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public TokenResponse generateTokens(AuthRequest request) throws CredentialException {

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + request.email() + " do not exists!"));

        if (!user.isEmailVerified())
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Email not verified!"
            );

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new CredentialException("Invalid Credentials!");


        var accessToken = generateAccessToken(user);
        var refreshToken = generateRefreshToken(user);

        return new TokenResponse(
                accessToken, ACCESS_EXPIRES_IN,
                refreshToken, REFRESH_EXPIRES_IN
        );
    }

    private String generateAccessToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(user.getId().toString())
                .claim("type", "access")
                .claim("roles", user.getRoles())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    private String generateRefreshToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(7 * 24 * 3600))
                .subject(user.getId().toString())
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    public AccessTokenResponse refreshToken(String refreshToken) {

        Jwt jwt = jwtDecoder.decode(refreshToken);

        if (!"refresh".equals(jwt.getClaim("type")))
            throw new RuntimeException("Invalid Token!");

        UUID userId = UUID.fromString(jwt.getSubject());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Do Not Exists!"));

        String newAccessToken = generateAccessToken(user);

        return new AccessTokenResponse(newAccessToken, ACCESS_EXPIRES_IN);
    }

    public String generateEmailToken(String userEmail) {
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("The user with email " + userEmail + " do not have an account on data!"));

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(86400))
                .subject(user.getId().toString())
                .claim("type", "email_verification")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    public void validateEmail(String token) {

        Jwt jwt = jwtDecoder.decode(token);

        if (!"email_verification".equals(jwt.getClaim("type"))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid token type"
            );
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId).orElseThrow(() ->
                        new EntityNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already verified"
            );
        }

        user.setEmailVerified(true);
        userRepository.save(user);
    }

}
