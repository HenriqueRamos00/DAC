package br.ufpr.bantads.saga.sagas.aprovacaocliente;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.AprovarClienteCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarContaCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarUsuarioClienteCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ListarGerentesAtivosCommand;
import br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.SelecionarGerenteParaNovaContaCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AprovacaoClienteCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.command}")
    private String criarUsuarioClienteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.command}")
    private String listarGerentesAtivosRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.selecionar-gerente-para-nova-conta.command}")
    private String selecionarGerenteParaNovaContaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.criar.command}")
    private String criarContaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.command}")
    private String aprovarClienteRoutingKey;

    public void publishCriarUsuarioCliente(CriarUsuarioClienteCommand command) {
        log.info("Publicando comando criar usuário cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, criarUsuarioClienteRoutingKey, command);
    }

    public void publishListarGerentesAtivos(ListarGerentesAtivosCommand command) {
        log.info("Publicando comando listar gerentes ativos: {}", command);
        rabbitTemplate.convertAndSend(exchange, listarGerentesAtivosRoutingKey, command);
    }

    public void publishSelecionarGerenteParaNovaConta(SelecionarGerenteParaNovaContaCommand command) {
        log.info("Publicando comando selecionar gerente para nova conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, selecionarGerenteParaNovaContaRoutingKey, command);
    }

    public void publishCriarConta(CriarContaCommand command) {
        log.info("Publicando comando criar conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, criarContaRoutingKey, command);
    }

    public void publishAprovarCliente(AprovarClienteCommand command) {
        log.info("Publicando comando aprovar cliente: {}", command);
        rabbitTemplate.convertAndSend(exchange, aprovarClienteRoutingKey, command);
    }
}
