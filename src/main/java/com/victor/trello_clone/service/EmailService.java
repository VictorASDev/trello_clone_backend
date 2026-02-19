package com.victor.trello_clone.service;

import com.victor.trello_clone.data.record.EmailRequest;
import com.victor.trello_clone.mail.EmailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

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

    public void sendVerificationEmail(String userEmail) {

        var user = userService.findByEmail(userEmail);
        var username = user.getUsername();
        var emailToken = authService.generateEmailToken(userEmail);

        var verificationUrl = "http://localhost:3000/email-verified?token=" + emailToken;


        var body = buildVerificationEmail(username, verificationUrl);

        sender.send(
                new String[]{userEmail},
                "Verificação de Email! - Trello clone",
                body);
    }

    public String buildVerificationEmail(String username, String verificationUrl) {
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
                                           Se o botão não funcionar, copie e cole este link no seu navegador:
                                           <br><br>
                                           <a href="%s" style="color:#0C66E4;">
                                               %s
                                           </a>
                                           <br><br>
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
        """.formatted(username, verificationUrl, verificationUrl, verificationUrl);
    }

}
