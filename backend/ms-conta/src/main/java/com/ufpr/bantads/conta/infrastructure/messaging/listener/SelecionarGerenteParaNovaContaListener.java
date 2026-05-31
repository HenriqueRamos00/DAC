package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ufpr.bantads.conta.application.dto.command.SelecionarGerenteParaNovaContaCommand;
import com.ufpr.bantads.conta.application.dto.event.GerenteParaNovaContaSelecionadoEvent;
import com.ufpr.bantads.conta.application.dto.event.SelecaoGerenteParaNovaContaFalhouEvent;
import com.ufpr.bantads.conta.application.dto.shared.GerenteCandidato;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelecionarGerenteParaNovaContaListener {

    private final ContaCommandRepository contaRepository;
    private final ContaEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.selecionar-gerente-para-nova-conta.command}")
    public void handle(SelecionarGerenteParaNovaContaCommand command) {
        log.info("Recebido SelecionarGerenteParaNovaContaCommand saga {}", command.sagaId());

        try {
            GerenteCandidato gerente = selecionar(command);
            publisher.publishGerenteParaNovaContaSelecionado(
                GerenteParaNovaContaSelecionadoEvent.from(command.sagaId(), gerente)
            );
        } catch (Exception ex) {
            log.warn("Falha ao selecionar gerente para nova conta saga {}: {}", command.sagaId(), ex.getMessage());
            publisher.publishSelecaoGerenteParaNovaContaFalhou(
                new SelecaoGerenteParaNovaContaFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }

    private GerenteCandidato selecionar(SelecionarGerenteParaNovaContaCommand command) {
        if (command.gerentes() == null || command.gerentes().isEmpty()) {
            throw new IllegalArgumentException("Nenhum gerente disponível");
        }

        Map<String, Long> totaisPorGerente = contarClientesPorGerente();
        GerenteCandidato gerenteParaNovaConta = command.gerentes().get(0);
        long menorTotal = totalClientes(gerenteParaNovaConta, totaisPorGerente);

        for (GerenteCandidato gerente : command.gerentes()) {
            long total = totalClientes(gerente, totaisPorGerente);

            if (total < menorTotal) {
                gerenteParaNovaConta = gerente;
                menorTotal = total;
            }
        }

        return gerenteParaNovaConta;
    }

    private Map<String, Long> contarClientesPorGerente() {
        Map<String, Long> totais = new HashMap<>();

        for (ContaCommand conta : contaRepository.findAll()) {
            String gerenteCpf = conta.getGerenteCpf();

            if (!totais.containsKey(gerenteCpf)) {
                totais.put(gerenteCpf, 0L);
            }

            totais.put(gerenteCpf, totais.get(gerenteCpf) + 1);
        }

        return totais;
    }

    private long totalClientes(
        GerenteCandidato gerente,
        Map<String, Long> totaisPorGerente
    ) {
        return totaisPorGerente.getOrDefault(gerente.cpf(), 0L);
    }
}
