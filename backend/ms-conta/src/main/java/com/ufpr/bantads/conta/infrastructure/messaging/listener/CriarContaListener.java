package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.CriarContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.event.CriacaoContaFalhouEvent;
import com.ufpr.bantads.conta.application.usecase.CriarContaUseCase;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CriarContaListener {

    private final CriarContaUseCase criarContaUseCase;
    private final ContaEventPublisher contaEventPublisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.criar.command}")
    public void handle(CriarContaCommand command) {
        log.info("Recebido comando de criação de conta para CPF {}", command.clienteCpf());

        try {
            ContaCriadaEvent event = criarContaUseCase.execute(command);
            contaEventPublisher.publishContaCriadaSaga(event);
        } catch (Exception ex) {
            log.warn("Falha ao criar conta para CPF {}: {}", command.clienteCpf(), ex.getMessage());
            contaEventPublisher.publishCriacaoContaFalhou(
                new CriacaoContaFalhouEvent(command.sagaId(), command.clienteCpf(), ex.getMessage())
            );
        }
    }
}
