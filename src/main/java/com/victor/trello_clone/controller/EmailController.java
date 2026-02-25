package com.victor.trello_clone.controller;

import com.victor.trello_clone.controller.docs.EmailControllerDocs;
import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.data.record.TokenRequest;
import com.victor.trello_clone.data.record.ValidateEmailRequest;
import com.victor.trello_clone.service.AuthService;
import com.victor.trello_clone.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController implements EmailControllerDocs {

    private final EmailService service;
    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        service.sendSimpleEmail(emailRequest);

        return ResponseEntity.ok().body("E-mail sent with success!");
    }

    @Override
    public ResponseEntity<String> sendEmailWithAttachment(String emailRequestJson, MultipartFile file) {
        return null;
    }

    @PostMapping("/send/token")
    @Override
    public ResponseEntity<?> sendVerification(@RequestBody ValidateEmailRequest req) {

        service.sendVerificationEmail(req.userEmail());

        return ResponseEntity.ok(
                Map.of("message",
                        "Se o email existir, enviaremos instruções."));
    }


    @PatchMapping("/send/validation")
    @Override
    public ResponseEntity<Void> validateEmailToken(@RequestBody TokenRequest request) {
        service.validateEmail(request.token());

        return ResponseEntity.ok().build();
    }
}