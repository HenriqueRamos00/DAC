package br.ufpr.bantads.saga.infrastructure.messaging.publisher;

import br.ufpr.bantads.saga.application.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.application.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.application.dto.command.InserirGerenteCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InsercaoGerenteCommandPublisher {

    private static final Logger log = LoggerFactory.getLogger(InsercaoGerenteCommandPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.command}")
    private String consultarGerenteMaisContasRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.command}")
    private String inserirGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.command}")
    private String atribuirGerenteContaRoutingKey;

    public InsercaoGerenteCommandPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

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
