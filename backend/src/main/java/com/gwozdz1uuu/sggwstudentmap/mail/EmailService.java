package com.gwozdz1uuu.sggwstudentmap.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String confirmationUrl = "http://localhost:8080/auth/confirm?token=" + token; 
        
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject("Potwierdź rejestrację - SGGW Student Map");
        email.setText("Witaj, kliknij w poniższy link, aby aktywować swoje konto:\n" + confirmationUrl);
        
        mailSender.send(email);
    }
}