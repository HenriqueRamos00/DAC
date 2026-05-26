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
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.AtribuicaoGerenteContaFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClienteAlteracaoFalhouEvent;
import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
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

    @Value("${saga.rabbitmq.queue.remocao-gerente.response}")
    private String remocaoGerenteResponseQueue;

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
    public Queue remocaoGerenteResponseQueue() {
        return QueueBuilder.durable(remocaoGerenteResponseQueue).build();
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
