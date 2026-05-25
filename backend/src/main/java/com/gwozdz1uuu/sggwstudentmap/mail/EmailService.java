package com.gwozdz1uuu.sggwstudentmap.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendVerificationEmail(String to, String token) {
        String confirmationUrl = frontendUrl + "/verify?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Potwierdź rejestrację - SGGW Student Map");

            String htmlContent = "<div style='font-family: Arial, sans-serif; text-align: center; padding: 20px;'>"
                    + "<h2 style='color: #2c3e50;'>Witaj w SGGW Student Map!</h2>"
                    + "<p style='font-size: 16px; color: #34495e;'>Aby aktywować swoje konto, kliknij poniższy przycisk:</p>"
                    + "<br>"
                    + "<a href='" + confirmationUrl + "' style='background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;'>Aktywuj konto</a>"
                    + "<br><br><p style='font-size: 12px; color: #7f8c8d;'>Link: " + confirmationUrl + "</p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Błąd wysyłki maila: " + e.getMessage());
        }
    }
}