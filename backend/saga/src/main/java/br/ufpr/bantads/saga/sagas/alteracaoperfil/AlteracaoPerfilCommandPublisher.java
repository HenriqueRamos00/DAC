package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoPerfilClienteCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlteracaoPerfilCommandPublisher {
    
    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.command}")
    private String alterarPerfilClienteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.command}")
    private String alterarLimiteContaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.command}")
    private String reverterAlteracaoPerfilClienteRoutingKey;

    public void publishAlterarPerfil(AlterarPerfilClienteCommand command) {
        log.info("Publicando comando de alteração de perfil do cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, alterarPerfilClienteRoutingKey, command);
    }

    public void publishAlterarLimite(AlterarLimiteContaCommand command) {
        log.info("Publicando comando de alteração de limite da conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, alterarLimiteContaRoutingKey, command);
    }

    public void publishReverterAlteracaoPerfil(ReverterAlteracaoPerfilClienteCommand command) {
        log.info("Publicando comando de compensação da alteração de perfil do cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, reverterAlteracaoPerfilClienteRoutingKey, command);
    }

}
