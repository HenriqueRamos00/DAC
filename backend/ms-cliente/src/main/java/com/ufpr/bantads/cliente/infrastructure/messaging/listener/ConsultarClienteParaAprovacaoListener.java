package com.ufpr.bantads.cliente.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.cliente.application.dto.command.ConsultarClienteParaAprovacaoCommand;
import com.ufpr.bantads.cliente.application.dto.event.ClienteConsultadoParaAprovacaoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ConsultaClienteParaAprovacaoFalhouEvent;
import com.ufpr.bantads.cliente.application.usecase.GetClienteByCpfUseCase;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.infrastructure.messaging.publisher.ConsultarClienteParaAprovacaoEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsultarClienteParaAprovacaoListener {

    private final GetClienteByCpfUseCase getClienteByCpfUseCase;
    private final ConsultarClienteParaAprovacaoEventPublisher eventPublisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.cliente.consultar-para-aprovacao.command}")
    public void handle(ConsultarClienteParaAprovacaoCommand command) {
        log.info("Recebido comando para consultar cliente para aprovacao com CPF {}", command.cpf());

        try {
            var cliente = getClienteByCpfUseCase.executeAndReturnEntity(command.cpf());
            if (cliente.getStatus() != StatusCliente.PENDENTE) {
                throw new ClienteNaoPendenteException(command.cpf());
            }

            eventPublisher.publishSucesso(
                ClienteConsultadoParaAprovacaoEvent.fromEntity(command.sagaId(), cliente)
            );
        } catch (Exception ex) {
            log.warn("Não foi possível consultar cliente {} para aprovação: {}", command.cpf(), ex.getMessage());
            eventPublisher.publishFalha(
                new ConsultaClienteParaAprovacaoFalhouEvent(command.sagaId(), command.cpf(), ex.getMessage())
            );
        }
    }
}
