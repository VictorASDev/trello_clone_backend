package com.victor.trello_clone.mail;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.model.user.User;
import com.victor.trello_clone.model.workspace.Workspace;
import com.victor.trello_clone.service.UserService;
import com.victor.trello_clone.service.WorkspaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static com.victor.trello_clone.mail.templates.EmailTemplates.buildVerificationEmail;
import static com.victor.trello_clone.mail.templates.EmailTemplates.buildWorkspaceInviteEmail;

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

    private void sendVerificationEmail(String userEmail) {

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

    public String generateWorkspaceInviteToken(String userEmail,UUID userId, UUID workspaceId) {
        Instant now = Instant.now();


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(86400))
                .subject(userId.toString())
                .claim("type", "workspace_invitation")
                .claim("workspace_id", workspaceId)
                .claim("invited_email", userEmail)
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

        User user = userService.findById(UUID.fromString(jwt.getSubject()));

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already verified"
            );
        }

        user.setEmailVerified(true);
        userService.save(user);
    }

    @Async
    public void sendAsyncWorkspaceInvite(User invitedUser, Workspace workspace) {
        sendWorkspaceInvite(invitedUser, workspace);
    }

    private void sendWorkspaceInvite(User invitedUser, Workspace workspace) {

        var token = generateWorkspaceInviteToken(
                invitedUser.getEmail(),
                invitedUser.getId(),
                workspace.getWorkspaceId());

        String verificationUrl = frontendUrl + "/invitations/accept?token=" + token;

        var body = buildWorkspaceInviteEmail(
                invitedUser.getUsername(),
                workspace.getName(),
                verificationUrl);

        System.out.println("Token = " + token);

        sender.send(
                new String[]{invitedUser.getEmail()},
                "Collaboration invite for " + workspace.getName() + "´s team",
                body
        );
    }

}
