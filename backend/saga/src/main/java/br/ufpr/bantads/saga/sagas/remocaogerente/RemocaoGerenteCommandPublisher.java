package br.ufpr.bantads.saga.sagas.remocaogerente;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ListarGerentesAtivosCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReatribuirContasGerenteCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.RemoverGerenteCommand;

// Compensação saga remoção de gerente
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReverterReatribuicaoContasCompensacaoCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class RemocaoGerenteCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.command}")
    private String listarGerentesAtivosRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reatribuir-contas-gerente.command}")
    private String reatribuirContasGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.command}")
    private String removerGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reverter-reatribuicao-contas-compensacao.command}")
    private String reverterReatribuicaoContasCompensacaoRoutingKey;

    public void publishListarGerentesAtivos(ListarGerentesAtivosCommand command) {
        log.info("Publicando comando listar-gerentes-ativos: {}", command);
        rabbitTemplate.convertAndSend(exchange, listarGerentesAtivosRoutingKey, command);
    }

    public void publishReatribuirContasGerente(ReatribuirContasGerenteCommand command) {
        log.info("Publicando comando reatribuir-contas-gerente: {}", command);
        rabbitTemplate.convertAndSend(exchange, reatribuirContasGerenteRoutingKey, command);
    }

    public void publishRemoverGerente(RemoverGerenteCommand command) {
        log.info("Publicando comando remover-gerente: {}", command);
        rabbitTemplate.convertAndSend(exchange, removerGerenteRoutingKey, command);
    }

    public void publishReverterReatribuicaoContasCompensacao(ReverterReatribuicaoContasCompensacaoCommand command) {
        log.info("Publicando comando reverter-reatribuicao-contas-compensacao saga {} contas {}",
            command.getSagaId(),
            command.getNumerosContas() == null ? 0 : command.getNumerosContas().size());
        rabbitTemplate.convertAndSend(exchange, reverterReatribuicaoContasCompensacaoRoutingKey, command);
    }
}