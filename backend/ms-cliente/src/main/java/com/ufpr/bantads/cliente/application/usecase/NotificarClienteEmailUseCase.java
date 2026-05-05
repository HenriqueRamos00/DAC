package com.ufpr.bantads.cliente.application.usecase;

import com.ufpr.bantads.cliente.application.port.EmailSender;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificarClienteEmailUseCase {

    private final EmailSender emailSender;

    public void notificarAprovacao(Cliente cliente, String senhaGerada) {
        String subject = "BANTADS - Sua conta foi aprovada!";
        String body = """
            Ola, %s!

            Sua conta no BANTADS foi aprovada com sucesso.

            Seus dados de acesso:
            - Email: %s
            - Senha: %s

            Atenciosamente,
            Equipe BANTADS
            """.formatted(cliente.getNome(), cliente.getEmail(), senhaGerada);

        emailSender.send(cliente.getEmail(), subject, body);
    }

    public void notificarRejeicao(Cliente cliente) {
        String subject = "BANTADS - Resultado da sua solicitacao de cadastro";
        String body = """
            Ola, %s!

            Infelizmente, sua solicitação de cadastro no BANTADS foi recusada.

            Motivo: %s

            Caso tenha dúvidas, entre em contato com o suporte.

            Atenciosamente,
            Equipe BANTADS
            """.formatted(cliente.getNome(), cliente.getMotivoRejeicao());

        emailSender.send(cliente.getEmail(), subject, body);
    }

    public void notificarFalhaAutocadastro(Cliente cliente, String motivo) {
        String subject = "BANTADS - Erro no processamento do seu cadastro";
        String body = """
            Ola, %s!

            Houve um problema ao processar seu cadastro no BANTADS.

            Motivo: %s

            Por favor, tente novamente mais tarde ou entre em contato com o suporte.

            Atenciosamente,
            Equipe BANTADS
            """.formatted(cliente.getNome(), motivo);

        emailSender.send(cliente.getEmail(), subject, body);
    }
}
