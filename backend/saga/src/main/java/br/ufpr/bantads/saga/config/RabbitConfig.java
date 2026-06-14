package br.ufpr.bantads.saga.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarLimiteContaCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.AlterarPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command.ReverterAlteracaoPerfilClienteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilRevertidoEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteReversaoPerfilFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ConsultaGerenteMaisContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ContaLimiteAlteradoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteAtribuidoContaEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteMaisContasConsultadoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.InsercaoGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ListarGerentesAtivosCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReatribuirContasGerenteCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.RemoverGerenteCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ContasReatribuidasEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerenteRemovidoEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.GerentesAtivosListadosEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ListagemGerentesAtivosFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReatribuicaoContasFalhouEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.RemocaoGerenteFalhouEvent;

// Saga Inserir Gerente - auth step
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.CriarUsuarioGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.CriacaoUsuarioGerenteFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteCriadoEvent;

// Saga Inserir Gerente - compensação
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ExcluirUsuarioGerenteCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.RemoverGerenteCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.ExclusaoUsuarioGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteRemovidoCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.RemocaoGerenteCompensacaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.UsuarioGerenteExcluidoCompensacaoEvent;

// Saga Remoção de Gerente - compensação reverter reatribuição
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.command.ReverterReatribuicaoContasCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReatribuicaoContasRevertidaCompensacaoEvent;
import br.ufpr.bantads.saga.sagas.remocaogerente.dto.event.ReversaoReatribuicaoContasCompensacaoFalhouEvent;

@Configuration
public class RabbitConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.queue.inserir-gerente.response}")
    private String inserirGerenteResponseQueue;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.sucesso}")
    private String consultarGerenteMaisContasSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.falha}")
    private String consultarGerenteMaisContasFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.sucesso}")
    private String gerenteInserirSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.falha}")
    private String gerenteInserirFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.sucesso}")
    private String contaAtribuirGerenteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.falha}")
    private String contaAtribuirGerenteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-gerente.sucesso}")
    private String authCriarUsuarioGerenteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-gerente.falha}")
    private String authCriarUsuarioGerenteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover-compensacao.sucesso}")
    private String gerenteRemoverCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover-compensacao.falha}")
    private String gerenteRemoverCompensacaoFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-gerente-compensacao.sucesso}")
    private String authExcluirUsuarioGerenteCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-gerente-compensacao.falha}")
    private String authExcluirUsuarioGerenteCompensacaoFalhaRoutingKey;

    @Value("${saga.rabbitmq.queue.alteracao-perfil.response}")
    private String alteracaoPerfilResponseQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.sucesso}")
    private String clienteAlterarPerfilSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.falha}")
    private String clienteAlterarPerfilFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.sucesso}")
    private String contaAlterarLimiteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.falha}")
    private String contaAlterarLimiteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.sucesso}")
    private String clienteReverterAlteracaoPerfilSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.falha}")
    private String clienteReverterAlteracaoPerfilFalhaRoutingKey;

    @Value("${saga.rabbitmq.queue.remocao-gerente.response}")
    private String remocaoGerenteResponseQueue;

    @Value("${saga.rabbitmq.queue.aprovacao-cliente.response}")
    private String aprovacaoClienteResponseQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.consultar-para-aprovacao.sucesso}")
    private String clienteConsultarParaAprovacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.consultar-para-aprovacao.falha}")
    private String clienteConsultarParaAprovacaoFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.sucesso}")
    private String authCriarUsuarioClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.falha}")
    private String authCriarUsuarioClienteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-cliente.sucesso}")
    private String authExcluirUsuarioClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-cliente.falha}")
    private String authExcluirUsuarioClienteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.sucesso}")
    private String gerenteListarAtivosDetalhadoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.falha}")
    private String gerenteListarAtivosDetalhadoFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.selecionar-gerente-para-nova-conta.sucesso}")
    private String contaSelecionarGerenteParaNovaContaSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.selecionar-gerente-para-nova-conta.falha}")
    private String contaSelecionarGerenteParaNovaContaFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.criar.sucesso}")
    private String contaCriarSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.criar.falha}")
    private String contaCriarFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.excluir-conta-cliente.sucesso}")
    private String contaExcluirContaClienteSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.excluir-conta-cliente.falha}")
    private String contaExcluirContaClienteFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.sucesso}")
    private String clienteAprovarSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.falha}")
    private String clienteAprovarFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.sucesso}")
    private String gerenteListarAtivosSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.falha}")
    private String gerenteListarAtivosFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reatribuir-contas-gerente.sucesso}")
    private String contaReatribuirContasSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reatribuir-contas-gerente.falha}")
    private String contaReatribuirContasFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.sucesso}")
    private String gerenteRemoverSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.falha}")
    private String gerenteRemoverFalhaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reverter-reatribuicao-contas-compensacao.sucesso}")
    private String contaReverterReatribuicaoContasCompensacaoSucessoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.reverter-reatribuicao-contas-compensacao.falha}")
    private String contaReverterReatribuicaoContasCompensacaoFalhaRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue inserirGerenteResponseQueue() {
        return QueueBuilder.durable(inserirGerenteResponseQueue).build();
    }

    @Bean
    public Binding consultarGerenteMaisContasSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(consultarGerenteMaisContasSucessoRoutingKey);
    }

    @Bean
    public Binding consultarGerenteMaisContasFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(consultarGerenteMaisContasFalhaRoutingKey);
    }

    @Bean
    public Binding gerenteInserirSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteInserirSucessoRoutingKey);
    }

    @Bean
    public Binding gerenteInserirFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteInserirFalhaRoutingKey);
    }

    @Bean
    public Binding contaAtribuirGerenteSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaAtribuirGerenteSucessoRoutingKey);
    }

    @Bean
    public Binding contaAtribuirGerenteFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaAtribuirGerenteFalhaRoutingKey);
    }

    @Bean
    public Binding authCriarUsuarioGerenteSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(authCriarUsuarioGerenteSucessoRoutingKey);
    }

    @Bean
    public Binding authCriarUsuarioGerenteFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(authCriarUsuarioGerenteFalhaRoutingKey);
    }

    @Bean
    public Binding gerenteRemoverCompensacaoSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteRemoverCompensacaoSucessoRoutingKey);
    }

    @Bean
    public Binding gerenteRemoverCompensacaoFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteRemoverCompensacaoFalhaRoutingKey);
    }

    @Bean
    public Binding authExcluirUsuarioGerenteCompensacaoSucessoBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(authExcluirUsuarioGerenteCompensacaoSucessoRoutingKey);
    }

    @Bean
    public Binding authExcluirUsuarioGerenteCompensacaoFalhaBinding(
        Queue inserirGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteResponseQueue)
            .to(sagaExchange)
            .with(authExcluirUsuarioGerenteCompensacaoFalhaRoutingKey);
    }

    @Bean
    public Queue alteracaoPerfilResponseQueue() {
        return QueueBuilder.durable(alteracaoPerfilResponseQueue).build();
    }

    @Bean
    public Binding clienteAlterarPerfilSucessoBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(clienteAlterarPerfilSucessoRoutingKey);
    }

    @Bean
    public Binding clienteAlterarPerfilFalhaBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(clienteAlterarPerfilFalhaRoutingKey);
    }

    @Bean
    public Binding contaAlterarLimiteSucessoBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(contaAlterarLimiteSucessoRoutingKey);
    }

    @Bean
    public Binding contaAlterarLimiteFalhaBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(contaAlterarLimiteFalhaRoutingKey);
    }

    @Bean
    public Binding clienteReverterAlteracaoPerfilSucessoBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(clienteReverterAlteracaoPerfilSucessoRoutingKey);
    }

    @Bean
    public Binding clienteReverterAlteracaoPerfilFalhaBinding(
        Queue alteracaoPerfilResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alteracaoPerfilResponseQueue)
            .to(sagaExchange)
            .with(clienteReverterAlteracaoPerfilFalhaRoutingKey);
    }

    @Bean
    public Queue remocaoGerenteResponseQueue() {
        return QueueBuilder.durable(remocaoGerenteResponseQueue).build();
    }

    @Bean
    public Queue aprovacaoClienteResponseQueue() {
        return QueueBuilder.durable(aprovacaoClienteResponseQueue).build();
    }

    @Bean
    public Binding clienteConsultarParaAprovacaoSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(clienteConsultarParaAprovacaoSucessoRoutingKey);
    }

    @Bean
    public Binding clienteConsultarParaAprovacaoFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(clienteConsultarParaAprovacaoFalhaRoutingKey);
    }

    @Bean
    public Binding authCriarUsuarioClienteSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(authCriarUsuarioClienteSucessoRoutingKey);
    }

    @Bean
    public Binding authCriarUsuarioClienteFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(authCriarUsuarioClienteFalhaRoutingKey);
    }

    @Bean
    public Binding authExcluirUsuarioClienteSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(authExcluirUsuarioClienteSucessoRoutingKey);
    }

    @Bean
    public Binding authExcluirUsuarioClienteFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(authExcluirUsuarioClienteFalhaRoutingKey);
    }

    @Bean
    public Binding gerenteListarAtivosDetalhadoSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(gerenteListarAtivosDetalhadoSucessoRoutingKey);
    }

    @Bean
    public Binding gerenteListarAtivosDetalhadoFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(gerenteListarAtivosDetalhadoFalhaRoutingKey);
    }

    @Bean
    public Binding contaSelecionarGerenteParaNovaContaSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaSelecionarGerenteParaNovaContaSucessoRoutingKey);
    }

    @Bean
    public Binding contaSelecionarGerenteParaNovaContaFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaSelecionarGerenteParaNovaContaFalhaRoutingKey);
    }

    @Bean
    public Binding contaCriarSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaCriarSucessoRoutingKey);
    }

    @Bean
    public Binding contaCriarFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaCriarFalhaRoutingKey);
    }

    @Bean
    public Binding contaExcluirContaClienteSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaExcluirContaClienteSucessoRoutingKey);
    }

    @Bean
    public Binding contaExcluirContaClienteFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(contaExcluirContaClienteFalhaRoutingKey);
    }

    @Bean
    public Binding clienteAprovarSucessoBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(clienteAprovarSucessoRoutingKey);
    }

    @Bean
    public Binding clienteAprovarFalhaBinding(
        Queue aprovacaoClienteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovacaoClienteResponseQueue)
            .to(sagaExchange)
            .with(clienteAprovarFalhaRoutingKey);
    }

    @Bean
    public Binding gerenteListarAtivosSucessoBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteListarAtivosSucessoRoutingKey);
    }

    @Bean
    public Binding gerenteListarAtivosFalhaBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteListarAtivosFalhaRoutingKey);
    }

    @Bean
    public Binding contaReatribuirContasSucessoBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaReatribuirContasSucessoRoutingKey);
    }

    @Bean
    public Binding contaReatribuirContasFalhaBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaReatribuirContasFalhaRoutingKey);
    }

    @Bean
    public Binding gerenteRemoverSucessoBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteRemoverSucessoRoutingKey);
    }

    @Bean
    public Binding gerenteRemoverFalhaBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(gerenteRemoverFalhaRoutingKey);
    }

    @Bean
    public Binding contaReverterReatribuicaoContasCompensacaoSucessoBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaReverterReatribuicaoContasCompensacaoSucessoRoutingKey);
    }

    @Bean
    public Binding contaReverterReatribuicaoContasCompensacaoFalhaBinding(
        Queue remocaoGerenteResponseQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(remocaoGerenteResponseQueue)
            .to(sagaExchange)
            .with(contaReverterReatribuicaoContasCompensacaoFalhaRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        jsonConverter.setClassMapper(sagaClassMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper sagaClassMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("cliente.alterar-perfil.command", AlterarPerfilClienteCommand.class);
        idClassMapping.put("conta.alterar-limite.command", AlterarLimiteContaCommand.class);
        idClassMapping.put("cliente.perfil-alterado", ClientePerfilAlteradoEvent.class);
        idClassMapping.put("cliente.alteracao-perfil.falhou", ClienteAlteracaoFalhouEvent.class);
        idClassMapping.put("conta.limite-alterado", ContaLimiteAlteradoEvent.class);
        idClassMapping.put("conta.operacao.limite-alterado", ContaLimiteAlteradoEvent.class);
        idClassMapping.put("conta.alteracao-limite.falhou", ClienteAlteracaoFalhouEvent.class);
        idClassMapping.put("cliente.reverter-alteracao-perfil", ReverterAlteracaoPerfilClienteCommand.class);
        idClassMapping.put("cliente.perfil-revertido", ClientePerfilRevertidoEvent.class);
        idClassMapping.put("cliente.reversao-perfil.falhou", ClienteReversaoPerfilFalhouEvent.class);

        // Saga Inserir Gerente
        idClassMapping.put("conta.consultar-gerente-mais-contas", ConsultarGerenteMaisContasCommand.class);
        idClassMapping.put("conta.gerente-mais-contas-consultado", GerenteMaisContasConsultadoEvent.class);
        idClassMapping.put("conta.consulta-gerente-mais-contas.falhou", ConsultaGerenteMaisContasFalhouEvent.class);
        idClassMapping.put("gerente.inserir", InserirGerenteCommand.class);
        idClassMapping.put("gerente.inserido", GerenteInseridoEvent.class);
        idClassMapping.put("gerente.insercao.falhou", InsercaoGerenteFalhouEvent.class);
        idClassMapping.put("conta.atribuir-gerente", AtribuirGerenteContaCommand.class);
        idClassMapping.put("conta.gerente-atribuido", GerenteAtribuidoContaEvent.class);
        idClassMapping.put("conta.atribuicao-gerente.falhou", AtribuicaoGerenteContaFalhouEvent.class);
        idClassMapping.put("auth.criar-usuario-gerente.command", CriarUsuarioGerenteCommand.class);
        idClassMapping.put("auth.usuario-gerente-criado", UsuarioGerenteCriadoEvent.class);
        idClassMapping.put("auth.criacao-usuario-gerente.falhou", CriacaoUsuarioGerenteFalhouEvent.class);
        idClassMapping.put("gerente.remover-compensacao", RemoverGerenteCompensacaoCommand.class);
        idClassMapping.put("gerente.removido-compensacao", GerenteRemovidoCompensacaoEvent.class);
        idClassMapping.put("gerente.remocao-compensacao.falhou", RemocaoGerenteCompensacaoFalhouEvent.class);
        idClassMapping.put("auth.excluir-usuario-gerente-compensacao", ExcluirUsuarioGerenteCompensacaoCommand.class);
        idClassMapping.put("auth.usuario-gerente-excluido-compensacao", UsuarioGerenteExcluidoCompensacaoEvent.class);
        idClassMapping.put("auth.exclusao-usuario-gerente-compensacao.falhou", ExclusaoUsuarioGerenteCompensacaoFalhouEvent.class);

        // Saga Remoção de Gerente
        idClassMapping.put("gerente.listar-ativos", ListarGerentesAtivosCommand.class);
        idClassMapping.put("gerente.ativos-listados", GerentesAtivosListadosEvent.class);
        idClassMapping.put("gerente.listagem-ativos.falhou", ListagemGerentesAtivosFalhouEvent.class);
        idClassMapping.put("conta.reatribuir-contas-gerente", ReatribuirContasGerenteCommand.class);
        idClassMapping.put("conta.contas-reatribuidas", ContasReatribuidasEvent.class);
        idClassMapping.put("conta.reatribuicao-contas.falhou", ReatribuicaoContasFalhouEvent.class);
        idClassMapping.put("gerente.remover", RemoverGerenteCommand.class);
        idClassMapping.put("gerente.removido", GerenteRemovidoEvent.class);
        idClassMapping.put("gerente.remocao.falhou", RemocaoGerenteFalhouEvent.class);
        idClassMapping.put("conta.reverter-reatribuicao-contas-compensacao", ReverterReatribuicaoContasCompensacaoCommand.class);
        idClassMapping.put("conta.reatribuicao-contas-revertida-compensacao", ReatribuicaoContasRevertidaCompensacaoEvent.class);
        idClassMapping.put("conta.reversao-reatribuicao-contas-compensacao.falhou", ReversaoReatribuicaoContasCompensacaoFalhouEvent.class);

        // Saga Aprovação de Cliente
        idClassMapping.put("cliente.consultar-para-aprovacao", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ConsultarClienteParaAprovacaoCommand.class);
        idClassMapping.put("cliente.consultado-para-aprovacao", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteConsultadoParaAprovacaoEvent.class);
        idClassMapping.put("cliente.consulta-para-aprovacao.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ConsultaClienteParaAprovacaoFalhouEvent.class);
        idClassMapping.put("auth.criar-usuario-cliente.command", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarUsuarioClienteCommand.class);
        idClassMapping.put("auth.usuario-cliente-criado", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.UsuarioClienteCriadoEvent.class);
        idClassMapping.put("auth.criacao-usuario-cliente.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoUsuarioClienteFalhouEvent.class);
        idClassMapping.put("auth.excluir-usuario-cliente.command", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ExcluirUsuarioClienteCommand.class);
        idClassMapping.put("auth.usuario-cliente-excluido", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.UsuarioClienteExcluidoEvent.class);
        idClassMapping.put("auth.exclusao-usuario-cliente.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ExclusaoUsuarioClienteFalhouEvent.class);
        idClassMapping.put("gerente.listar-ativos-detalhado", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ListarGerentesAtivosCommand.class);
        idClassMapping.put("gerente.ativos-detalhados-listados", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerentesAtivosListadosEvent.class);
        idClassMapping.put("gerente.listagem-ativos-detalhados.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ListagemGerentesAtivosFalhouEvent.class);
        idClassMapping.put("conta.selecionar-gerente-para-nova-conta", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.SelecionarGerenteParaNovaContaCommand.class);
        idClassMapping.put("conta.gerente-para-nova-conta-selecionado", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.GerenteParaNovaContaSelecionadoEvent.class);
        idClassMapping.put("conta.selecao-gerente-para-nova-conta.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.SelecaoGerenteParaNovaContaFalhouEvent.class);
        idClassMapping.put("conta.criar.command", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.CriarContaCommand.class);
        idClassMapping.put("conta.criada", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ContaCriadaSagaEvent.class);
        idClassMapping.put("conta.criacao.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.CriacaoContaFalhouEvent.class);
        idClassMapping.put("conta.excluir-conta-cliente.command", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.ExcluirContaClienteCommand.class);
        idClassMapping.put("conta.conta-cliente-excluida", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ContaClienteExcluidaEvent.class);
        idClassMapping.put("conta.exclusao-conta-cliente.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ExclusaoContaClienteFalhouEvent.class);
        idClassMapping.put("cliente.aprovar", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command.AprovarClienteCommand.class);
        idClassMapping.put("cliente.aprovado", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.ClienteAprovadoEvent.class);
        idClassMapping.put("cliente.aprovacao.falhou", br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.event.AprovacaoClienteFalhouEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
        ConnectionFactory connectionFactory,
        MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
