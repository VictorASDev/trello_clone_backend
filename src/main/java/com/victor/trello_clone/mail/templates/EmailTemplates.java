package com.victor.trello_clone.mail.templates;

public class EmailTemplates {

    public static String buildVerificationEmail(
            String username,
            String verificationUrl) {
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

    public static String buildWorkspaceInviteEmail(
            String username, String workspaceName, String inviteUrl) {
        return """
            
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Convite para Workspace</title>
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
                                            Você recebeu um convite
                                        </h2>
                                    </td>
                                </tr>
        
                                <tr>
                                    <td style="color:#44546F;font-size:14px;line-height:1.6;">
                                        Olá <b>%s</b>,
                                        <br><br>
                                        Você foi convidado para participar do workspace
                                        <b>%s</b>.
                                        <br><br>
                                        Clique no botão abaixo para aceitar o convite e
                                        começar a colaborar.
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
                                            Aceitar convite
                                        </a>
                                    </td>
                                </tr>
        
                                <tr>
                                    <td style="color:#6B778C;font-size:12px;">
                                        Se você não esperava esse convite, pode ignorar este email.
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
            """.formatted(username, workspaceName, inviteUrl);
    }
}
