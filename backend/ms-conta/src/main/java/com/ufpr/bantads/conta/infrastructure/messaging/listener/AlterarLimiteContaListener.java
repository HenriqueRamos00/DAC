package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaAlteracaoLimiteFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.application.usecase.AlterarLimiteContaUseCase;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlterarLimiteContaListener {

    private final AlterarLimiteContaUseCase alterarLimiteContaUseCase;
    private final ContaEventPublisher contaEventPublisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.alterar-limite.command}")
    public void handle(AlterarLimiteContaCommand command) {
        log.info("Recebido comando de alteração de limite para CPF {}", command.cpf());

        try {
            ContaLimiteAlteradoEvent event = alterarLimiteContaUseCase.execute(command);
            contaEventPublisher.publishLimiteAlteradoSaga(event);
        } catch (Exception ex) {
            log.warn("Falha ao alterar limite da conta do CPF {}: {}", command.cpf(), ex.getMessage());
            contaEventPublisher.publishAlteracaoLimiteFalhou(
                new ContaAlteracaoLimiteFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
