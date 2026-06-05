package com.victor.trello_clone.service;

import com.victor.trello_clone.data.record.SignUpRequest;
import com.victor.trello_clone.mail.EmailSender;
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
    private final UserService userService;
    private final EmailSender sender;

    public AuthService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       JwtDecoder jwtDecoder,
                       UserService userService,
                       EmailSender sender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userService = userService;
        this.sender = sender;
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

    public void sendVerificationEmail(String userEmail) {

        var userOpt = userService.findOptionalByEmail(userEmail);

        if (userOpt.isEmpty()) {
            return;
        }

        var user = userOpt.get();

        if (user.isEmailVerified()) {
            return;
        }

        if (recentlySent(user)) {
            return;
        }

        var emailToken = generateEmailToken(userEmail);

        //TODO: criar variável de ambiente
        var verificationUrl = "localhost:3000" + "/email-verified?token=" + emailToken;

        var body = buildVerificationEmail(user.getUsername(), verificationUrl);

        sender.send(
                new String[]{userEmail},
                "Verificação de Email! - Trello clone",
                body);

        user.setLastVerificationSentAt(Instant.now());
        userService.save(user);
    }

    public void signUp(SignUpRequest request) {
        userService.create(request);
        sendVerificationEmail(request.email());
    }

    private String buildVerificationEmail(String username, String verificationUrl) {
        return """
        <!DOCTYPE html>
               <html>
               <head>
                   <meta charset="UTF-8">
                   <title>Verificação de Conta!</title>
               </head>
               <body style="margin:0;padding:0;background:#f4f6f8;font-family:Arial,Helvetica,sans-serif;">
                   <table width="100%%" cellpadding="0" cellspacing="0" style="padding:40px 0;">
                       <tr>
                           <td align="center">
                               <table width="500" cellpadding="0" cellspacing="0"\s
                                      style="background:#ffffff;border-radius:8px;padding:40px;">
        
                                   <tr>
                                       <td align="center" style="padding-bottom:20px;">
                                           <h2 style="margin:0;color:#172B4D;">
                                               Verifique seu e-mail
                                           </h2>
                                       </td>
                                   </tr>
        
                                   <tr>
                                       <td style="color:#44546F;font-size:14px;line-height:1.6;">
                                           Olá <b>%s</b>,
                                           <br><br>
                                           Obrigado por se cadastrar.
                                           Confirme seu endereço de e-mail clicando no botão abaixo.
                                       </td>
                                   </tr>
        
                                   <tr>
                                       <td align="center" style="padding:30px 0;">
                                           <a href="%s"
                                              style="background:#0C66E4;
                                                     color:#ffffff;
                                                     text-decoration:none;
                                                     padding:12px 24px;
                                                     border-radius:6px;
                                                     font-weight:bold;
                                                     display:inline-block;">
                                               Verificar e-mail
                                           </a>
                                       </td>
                                   </tr>
        
                                   <tr>
                                       <td style="color:#6B778C;font-size:12px;line-height:1.5;">
                                           Este link expira em 24 horas.
                                       </td>
                                   </tr>
        
                                   <tr>
                                       <td align="center" style="padding-top:30px;color:#6B778C;font-size:12px;">
                                           Trello Clone
                                       </td>
                                   </tr>
        
                               </table>
                           </td>
                       </tr>
                   </table>
               </body>
               </html>
        """.formatted(username, verificationUrl, verificationUrl);
    }

    private boolean recentlySent(User user) {
        if (user.getLastVerificationSentAt() == null) return false;

        return user.getLastVerificationSentAt()
                .isAfter(Instant.now().minusSeconds(600)); // 10 min
    }

}
