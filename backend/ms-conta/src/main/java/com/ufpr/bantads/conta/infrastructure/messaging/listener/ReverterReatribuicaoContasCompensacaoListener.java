package com.ufpr.bantads.conta.infrastructure.messaging.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.ReverterReatribuicaoContasCompensacaoCommand;
import com.ufpr.bantads.conta.application.dto.event.ReatribuicaoContasRevertidaCompensacaoEvent;
import com.ufpr.bantads.conta.application.dto.event.ReversaoReatribuicaoContasCompensacaoFalhouEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.RemocaoGerenteCompensacaoEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReverterReatribuicaoContasCompensacaoListener {

    private final ContaCommandRepository commandRepository;
    private final ContaQueryRepository queryRepository;
    private final RemocaoGerenteCompensacaoEventPublisher publisher;

    @RabbitListener(queues = "${saga.rabbitmq.queue.conta.reverter-reatribuicao-contas-compensacao.command}")
    @Transactional
    public void handle(ReverterReatribuicaoContasCompensacaoCommand command) {
        log.info("Recebido ReverterReatribuicaoContasCompensacaoCommand saga {} origem {} destino {} contas {}",
            command.sagaId(), command.gerenteOriginalCpf(), command.gerenteDestinoCpf(),
            command.numerosContas() == null ? 0 : command.numerosContas().size());

        try {
            if (command.numerosContas() == null || command.numerosContas().isEmpty()) {
                publisher.publishReatribuicaoContasRevertidaCompensacao(
                    new ReatribuicaoContasRevertidaCompensacaoEvent(command.sagaId(), 0L)
                );
                return;
            }

            long revertidas = 0;
            for (String numero : command.numerosContas()) {
                ContaCommand conta = commandRepository.findByNumeroConta(numero).orElse(null);
                if (conta == null) {
                    log.warn("Conta {} não encontrada na compensação saga {}", numero, command.sagaId());
                    continue;
                }

                // Idempotência: se a conta já está no gerente original, não faz nada
                if (command.gerenteOriginalCpf().equals(conta.getGerenteCpf())) {
                    continue;
                }

                // Sanidade: só reverte se a conta ainda está no gerente destino esperado
                if (!command.gerenteDestinoCpf().equals(conta.getGerenteCpf())) {
                    log.warn("Conta {} saga {} está em gerente {} (esperado {}); pulando reversão",
                        numero, command.sagaId(), conta.getGerenteCpf(), command.gerenteDestinoCpf());
                    continue;
                }

                conta.setGerenteCpf(command.gerenteOriginalCpf());
                commandRepository.save(conta);

                ContaQuery view = queryRepository.findByNumeroConta(numero).orElse(null);
                if (view != null) {
                    view.setGerenteCpf(command.gerenteOriginalCpf());
                    queryRepository.save(view);
                }

                revertidas++;
            }

            publisher.publishReatribuicaoContasRevertidaCompensacao(
                new ReatribuicaoContasRevertidaCompensacaoEvent(command.sagaId(), revertidas)
            );
        } catch (Exception ex) {
            log.error("Falha ao reverter reatribuição de contas saga {}", command.sagaId(), ex);
            publisher.publishReversaoReatribuicaoContasCompensacaoFalhou(
                new ReversaoReatribuicaoContasCompensacaoFalhouEvent(command.sagaId(), ex.getMessage())
            );
        }
    }
}