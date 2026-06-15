package br.ufpr.bantads.saga.sagas.alteracaoperfil;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarUsuarioClienteAuthCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoUsuarioClienteAuthCommand;
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

    @Value("${saga.rabbitmq.routing-key.auth.alterar-usuario-cliente.command}")
    private String alterarUsuarioClienteAuthRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.command}")
    private String reverterAlteracaoPerfilClienteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.reverter-alteracao-usuario-cliente.command}")
    private String reverterAlteracaoUsuarioClienteAuthRoutingKey;

    public void publishAlterarPerfil(AlterarPerfilClienteCommand command) {
        log.info("Publicando comando de alteração de perfil do cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, alterarPerfilClienteRoutingKey, command);
    }

    public void publishAlterarLimite(AlterarLimiteContaCommand command) {
        log.info("Publicando comando de alteração de limite da conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, alterarLimiteContaRoutingKey, command);
    }

    public void publishAlterarUsuarioClienteAuth(AlterarUsuarioClienteAuthCommand command) {
        log.info("Publicando comando de alteração do usuário cliente no auth: {}", command);
        rabbitTemplate.convertAndSend(exchange, alterarUsuarioClienteAuthRoutingKey, command);
    }

    public void publishReverterAlteracaoPerfil(ReverterAlteracaoPerfilClienteCommand command) {
        log.info("Publicando comando de compensação da alteração de perfil do cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, reverterAlteracaoPerfilClienteRoutingKey, command);
    }

    public void publishReverterAlteracaoUsuarioClienteAuth(ReverterAlteracaoUsuarioClienteAuthCommand command) {
        log.info("Publicando comando de compensação do usuário cliente no auth: {}", command);
        rabbitTemplate.convertAndSend(exchange, reverterAlteracaoUsuarioClienteAuthRoutingKey, command);
    }

}
