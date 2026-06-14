package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import java.util.Comparator;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.ReatribuirContasGerenteCommand;
import com.ufpr.bantads.conta.application.dto.event.ContasReatribuidasEvent;
import com.ufpr.bantads.conta.application.dto.event.ReatribuicaoContasFalhouEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.RemocaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReatribuirContasGerenteListener {

    private final ContaCommandRepository commandRepository;
    private final ContaQueryRepository queryRepository;
    private final RemocaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.reatribuir-contas-gerente.command}")
    @Transactional
    public void handle(ReatribuirContasGerenteCommand command) {
        log.info("Recebido ReatribuirContasGerenteCommand saga {} origem {} candidatos {}",
            command.sagaId(), command.cpfOrigem(), command.candidatosDestino());

        try {
            if (command.candidatosDestino() == null || command.candidatosDestino().isEmpty()) {
                publisher.publishReatribuicaoContasFalhou(
                    new ReatribuicaoContasFalhouEvent(command.sagaId(), "Lista de candidatos destino vazia")
                );
                return;
            }

            String destino = command.candidatosDestino().stream()
                .min(Comparator.comparingLong(commandRepository::countByGerenteCpf))
                .orElseThrow();

            List<ContaCommand> contasOrigem = commandRepository.findAllByGerenteCpf(command.cpfOrigem());
            List<String> numerosMovidos = contasOrigem.stream()
                .map(ContaCommand::getNumeroConta)
                .toList();

            for (ContaCommand conta : contasOrigem) {
                conta.setGerenteCpf(destino);
            }
            commandRepository.saveAll(contasOrigem);

            List<ContaQuery> contasOrigemQuery = queryRepository.findByGerenteCpf(command.cpfOrigem());
            for (ContaQuery view : contasOrigemQuery) {
                view.setGerenteCpf(destino);
            }
            queryRepository.saveAll(contasOrigemQuery);

            publisher.publishContasReatribuidas(
                new ContasReatribuidasEvent(command.sagaId(), destino, (long) contasOrigem.size(), numerosMovidos)
            );
        } catch (Exception ex) {
            log.error("Falha ao reatribuir contas saga {}", command.sagaId(), ex);
            publisher.publishReatribuicaoContasFalhou(
                new ReatribuicaoContasFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}