package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.ConsultarGerenteMaisContasCommand;
import com.ufpr.bantads.conta.application.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import com.ufpr.bantads.conta.application.dto.event.GerenteMaisContasConsultadoEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.InsercaoGerenteEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsultarGerenteMaisContasListener {

    private final ContaCommandRepository contaRepository;
    private final InsercaoGerenteEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.consultar-gerente-mais-contas.command}")
    public void handle(ConsultarGerenteMaisContasCommand command) {
        log.info("Recebido ConsultarGerenteMaisContasCommand saga {}", command.sagaId());

        try {
            GerenteContas escolhido = buscarGerenteComMaisContas();

            if (escolhido == null) {
                publisher.publishConsultaGerenteMaisContasFalhou(
                    new ConsultaGerenteMaisContasFalhouEvent(command.sagaId(), "Nenhum gerente possui contas atribuídas")
                );
                return;
            }

            publisher.publishGerenteMaisContasConsultado(
                new GerenteMaisContasConsultadoEvent(command.sagaId(), escolhido.cpf, escolhido.total)
            );
        } catch (Exception ex) {
            log.error("Falha ao consultar gerente com mais contas saga {}", command.sagaId(), ex);
            publisher.publishConsultaGerenteMaisContasFalhou(
                new ConsultaGerenteMaisContasFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }

    private GerenteContas buscarGerenteComMaisContas() {
        Map<String, GerenteContas> agregado = new HashMap<>();

        for (ContaCommand conta : contaRepository.findAll()) {
            agregado.compute(conta.getGerenteCpf(), (cpf, atual) -> {
                if (atual == null) {
                    return new GerenteContas(cpf, 1L, conta.getDataCriacao());
                }
                LocalDateTime maxData = conta.getDataCriacao().isAfter(atual.maxDataCriacao)
                    ? conta.getDataCriacao()
                    : atual.maxDataCriacao;
                return new GerenteContas(cpf, atual.total + 1, maxData);
            });
        }

        return agregado.values().stream()
            .max(Comparator
                .comparingLong((GerenteContas g) -> g.total)
                .thenComparing(g -> g.maxDataCriacao))
            .orElse(null);
    }

    private static final class GerenteContas {
        final String cpf;
        final Long total;
        final LocalDateTime maxDataCriacao;

        GerenteContas(String cpf, Long total, LocalDateTime maxDataCriacao) {
            this.cpf = cpf;
            this.total = total;
            this.maxDataCriacao = maxDataCriacao;
        }
    }
}