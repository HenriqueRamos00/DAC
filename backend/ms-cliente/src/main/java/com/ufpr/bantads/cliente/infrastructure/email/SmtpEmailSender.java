package com.ufpr.bantads.cliente.infrastructure.email;

import com.ufpr.bantads.cliente.application.port.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    @Value("${bantads.mail.from}")
    private String from;

    @Override
    public void send(String to, String subject, String body) {
        log.info("Enviando email para {} com assunto '{}'", to, subject);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Email enviado com sucesso para {}", to);
    }
}
