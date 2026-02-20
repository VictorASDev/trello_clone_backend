package com.victor.trello_clone.service;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.mail.EmailSender;
import com.victor.trello_clone.model.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
public class EmailService {

    private final EmailSender sender;
    private final AuthService authService;
    private final UserService userService;

    public EmailService(EmailSender sender, AuthService authService, UserService userService) {
        this.sender = sender;
        this.authService = authService;
        this.userService = userService;
    }

    public void sendSimpleEmail(EmailRequest request) {

        String[] destinataryList = Arrays.stream(request.to().split("\\s*;\\s*"))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);


        sender.send(destinataryList,
                request.subject(),
                request.body());
    }

}
