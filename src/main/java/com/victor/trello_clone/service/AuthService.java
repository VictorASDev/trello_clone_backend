package com.victor.trello_clone.service;

import com.victor.trello_clone.model.User;
import com.victor.trello_clone.data.record.AccessTokenResponse;
import com.victor.trello_clone.repository.UserRepository;
import com.victor.trello_clone.data.record.AuthRequest;
import com.victor.trello_clone.data.record.TokenResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.security.auth.login.CredentialException;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private static final long ACCESS_EXPIRES_IN = 15 * 60;
    private static final long REFRESH_EXPIRES_IN = 7 * 24 * 3600;

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
        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath().build().toUriString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
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
        String issuerUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath().build().toUriString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuerUrl)
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

        if (!"refresh".equals(jwt.getClaim("type"))) {
            throw new RuntimeException("Invalid Token!");
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User Do Not Exists!"));

        String newAccessToken = generateAccessToken(user);

        return new AccessTokenResponse(newAccessToken, ACCESS_EXPIRES_IN);
    }
}
