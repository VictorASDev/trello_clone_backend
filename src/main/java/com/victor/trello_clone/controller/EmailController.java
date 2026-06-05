package com.victor.trello_clone.controller;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.data.record.TokenRequest;
import com.victor.trello_clone.data.record.ValidateEmailRequest;
import com.victor.trello_clone.service.AuthService;
import com.victor.trello_clone.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final EmailService service;
    public EmailController(EmailService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest emailRequest) {
        service.sendSimpleEmail(emailRequest);

        return ResponseEntity.ok().body("E-mail sent with success!");
    }

    public ResponseEntity<String> sendEmailWithAttachment(String emailRequestJson, MultipartFile file) {
        return null;
    }
}