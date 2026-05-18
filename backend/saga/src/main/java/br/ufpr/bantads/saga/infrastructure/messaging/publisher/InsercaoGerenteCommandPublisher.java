package br.ufpr.bantads.saga.infrastructure.messaging.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.application.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.application.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.application.dto.command.InserirGerenteCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.command}")
    private String consultarGerenteMaisContasRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.command}")
    private String inserirGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.command}")
    private String atribuirGerenteContaRoutingKey;

    public void publishConsultarGerenteMaisContas(ConsultarGerenteMaisContasCommand command) {
        log.info("Publicando comando consultar-gerente-mais-contas: {}", command);
        rabbitTemplate.convertAndSend(exchange, consultarGerenteMaisContasRoutingKey, command);
    }

    public void publishInserirGerente(InserirGerenteCommand command) {
        log.info("Publicando comando inserir-gerente: {}", command);
        rabbitTemplate.convertAndSend(exchange, inserirGerenteRoutingKey, command);
    }

    public void publishAtribuirGerenteConta(AtribuirGerenteContaCommand command) {
        log.info("Publicando comando atribuir-gerente-conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, atribuirGerenteContaRoutingKey, command);
    }
}