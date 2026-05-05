package com.ufpr.bantads.cliente.application.port;

public interface EmailSender {
    void send(String to, String subject, String body);
}
