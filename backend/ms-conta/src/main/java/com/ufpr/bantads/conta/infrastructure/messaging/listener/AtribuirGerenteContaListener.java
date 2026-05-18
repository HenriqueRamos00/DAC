package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.AtribuirGerenteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.AtribuicaoGerenteContaFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.GerenteAtribuidoContaEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.InsercaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AtribuirGerenteContaListener {

    private final ContaCommandRepository commandRepository;
    private final ContaQueryRepository queryRepository;
    private final InsercaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.atribuir-gerente.command}")
    @Transactional
    public void handle(AtribuirGerenteContaCommand command) {
        log.info("Recebido AtribuirGerenteContaCommand saga {}", command.sagaId());

        try {
            Optional<ContaCommand> alvo = commandRepository
                .findFirstByGerenteCpfOrderByDataCriacaoDesc(command.gerenteOriginalCpf());

            if (alvo.isEmpty()) {
                publisher.publishAtribuicaoGerenteContaFalhou(
                    new AtribuicaoGerenteContaFalhouEvent(
                        command.sagaId(),
                        "Gerente original " + command.gerenteOriginalCpf() + " não possui contas"
                    )
                );
                return;
            }

            ContaCommand conta = alvo.get();
            conta.setGerenteCpf(command.novoGerenteCpf());
            commandRepository.save(conta);

            queryRepository.findByNumeroConta(conta.getNumeroConta()).ifPresent(view -> {
                view.setGerenteCpf(command.novoGerenteCpf());
                queryRepository.save(view);
            });

            publisher.publishGerenteAtribuidoConta(
                new GerenteAtribuidoContaEvent(command.sagaId(), 1L)
            );
        } catch (Exception ex) {
            log.error("Falha ao atribuir gerente saga {}", command.sagaId(), ex);
            publisher.publishAtribuicaoGerenteContaFalhou(
                new AtribuicaoGerenteContaFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}