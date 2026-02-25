package com.victor.trello_clone.service;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.mail.EmailSender;
import com.victor.trello_clone.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class EmailService {

    private final EmailSender sender;
    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private static final String ISSUER = "trello-clone-api";


    @Value("${app.frontend.url}")
    private String frontendUrl;

    public EmailService(EmailSender sender,
                        UserService userService,
                        JwtEncoder jwtEncoder,
                        JwtDecoder jwtDecoder) {
        this.sender = sender;
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public void sendSimpleEmail(EmailRequest request) {

        String[] destinataryList = Arrays.stream(request.to().split("\\s*;\\s*"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);


        sender.send(destinataryList,
                request.subject(),
                request.body());
    }

    @Async
    public void sendVerificationEmailAsync(String userEmail) {
        sendVerificationEmail(userEmail);
    }

    public void sendVerificationEmail(String userEmail) {

        var userOpt = userService.findOptionalByEmail(userEmail);

        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        if (user.isEmailVerified()) return;
        if (recentlySent(user)) return;

        String token = generateEmailToken(userEmail);

        String verificationUrl = frontendUrl + "/email-verified?token=" + token;

        String body = buildVerificationEmail(user.getUsername(), verificationUrl);

        sender.send(
                new String[]{userEmail},
                "Verificação de Email! - Trello clone",
                body
        );

        user.setLastVerificationSentAt(Instant.now());
        userService.save(user);
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
                        <table width="500" cellpadding="0" cellspacing="0"
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
                                <td style="color:#6B778C;font-size:12px;">
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
        """.formatted(username, verificationUrl);
    }

    private boolean recentlySent(User user) {
        if (user.getLastVerificationSentAt() == null) return false;

        return user.getLastVerificationSentAt()
                .isAfter(Instant.now().minusSeconds(300));
    }

    public String generateEmailToken(String userEmail) {
        var user = userService.findByEmail(userEmail);

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

        User user = userService.findById(userId);

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already verified"
            );
        }

        user.setEmailVerified(true);
        userService.save(user);
    }


}
