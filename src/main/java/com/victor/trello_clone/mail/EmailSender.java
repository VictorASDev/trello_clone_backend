package com.victor.trello_clone.mail;

import com.victor.trello_clone.configuration.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class EmailSender {

    private final JavaMailSender mailSender;
    private final EmailConfig config;


    public EmailSender(JavaMailSender mailSender, EmailConfig config) {
        this.mailSender = mailSender;
        this.config = config;
    }

    public void send(String[] to, String subject, String body) {
        send(to, subject, body, null);
    }

    public void send(String[] to, String subject, String body, File attachment) {
        MimeMessage message = mailSender.createMimeMessage();

       try {
           MimeMessageHelper helper = new MimeMessageHelper(message, true);
           helper.setFrom(config.getFrom());
           helper.setTo(to);
           helper.setSubject(subject);
           helper.setText(body);

           if (attachment != null)
               helper.addAttachment(attachment.getName(), attachment);

           mailSender.send(message);

       } catch (MessagingException e) {
            throw new RuntimeException("Error sending the email!", e);
       }

    }
}
