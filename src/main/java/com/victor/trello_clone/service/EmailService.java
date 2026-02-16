package com.victor.trello_clone.service;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.mail.EmailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {

    private final EmailSender sender;

    public EmailService(EmailSender sender) {
        this.sender = sender;
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
