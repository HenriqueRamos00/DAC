package com.ufpr.bantads.cliente.infrastructure.config;

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

import com.ufpr.bantads.cliente.application.dto.command.AlterarPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.command.AprovarClienteCommand;
import com.ufpr.bantads.cliente.application.dto.command.ConsultarClienteParaAprovacaoCommand;
import com.ufpr.bantads.cliente.application.dto.command.NotificarFalhaAutocadastroCommand;
import com.ufpr.bantads.cliente.application.dto.command.ReverterAlteracaoPerfilClienteCommand;
import com.ufpr.bantads.cliente.application.dto.event.AprovacaoClienteFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteAlteracaoFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteAprovadoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteConsultadoParaAprovacaoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilAlteradoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClientePerfilRevertidoEvent;
import com.ufpr.bantads.cliente.application.dto.event.ClienteReversaoPerfilFalhouEvent;
import com.ufpr.bantads.cliente.application.dto.event.ConsultaClienteParaAprovacaoFalhouEvent;

@Configuration
public class RabbitMQConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.queue.cliente.aprovar.command}")
    private String aprovarClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.command}")
    private String aprovarClienteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.consultar-para-aprovacao.command}")
    private String consultarClienteParaAprovacaoCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.consultar-para-aprovacao.command}")
    private String consultarClienteParaAprovacaoCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.rejeitar.command}")
    private String rejeitarClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.rejeitar.command}")
    private String rejeitarClienteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.alterar-perfil.command}")
    private String alterarPerfilClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.alterar-perfil.command}")
    private String alterarPerfilClienteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.reverter-alteracao-perfil.command}")
    private String reverterAlteracaoPerfilClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.reverter-alteracao-perfil.command}")
    private String reverterAlteracaoPerfilClienteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.notificar-falha-autocadastro.command}")
    private String notificarFalhaAutocadastroCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.notificar-falha-autocadastro.command}")
    private String notificarFalhaAutocadastroCommandRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue aprovarClienteCommandQueue() {
        return QueueBuilder.durable(aprovarClienteCommandQueue).build();
    }

    @Bean
    public Queue consultarClienteParaAprovacaoCommandQueue() {
        return QueueBuilder.durable(consultarClienteParaAprovacaoCommandQueue).build();
    }

    @Bean
    public Queue rejeitarClienteCommandQueue() {
        return QueueBuilder.durable(rejeitarClienteCommandQueue).build();
    }

    @Bean
    public Queue alterarPerfilClienteCommandQueue() {
        return QueueBuilder.durable(alterarPerfilClienteCommandQueue).build();
    }

    @Bean
    public Queue reverterAlteracaoPerfilClienteCommandQueue() {
        return QueueBuilder.durable(reverterAlteracaoPerfilClienteCommandQueue).build();
    }

    @Bean
    public Queue notificarFalhaAutocadastroCommandQueue() {
        return QueueBuilder.durable(notificarFalhaAutocadastroCommandQueue).build();
    }

    @Bean
    public Binding aprovarClienteCommandBinding(
        Queue aprovarClienteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(aprovarClienteCommandQueue)
            .to(sagaExchange)
            .with(aprovarClienteCommandRoutingKey);
    }

    @Bean
    public Binding consultarClienteParaAprovacaoCommandBinding(
        Queue consultarClienteParaAprovacaoCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(consultarClienteParaAprovacaoCommandQueue)
            .to(sagaExchange)
            .with(consultarClienteParaAprovacaoCommandRoutingKey);
    }

    @Bean
    public Binding rejeitarClienteCommandBinding(
        Queue rejeitarClienteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(rejeitarClienteCommandQueue)
            .to(sagaExchange)
            .with(rejeitarClienteCommandRoutingKey);
    }

    @Bean
    public Binding alterarPerfilClienteCommandBinding(
        Queue alterarPerfilClienteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alterarPerfilClienteCommandQueue)
            .to(sagaExchange)
            .with(alterarPerfilClienteCommandRoutingKey);
    }

    @Bean
    public Binding reverterAlteracaoPerfilClienteCommandBinding(
        Queue reverterAlteracaoPerfilClienteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(reverterAlteracaoPerfilClienteCommandQueue)
            .to(sagaExchange)
            .with(reverterAlteracaoPerfilClienteCommandRoutingKey);
    }

    @Bean
    public Binding notificarFalhaAutocadastroCommandBinding(
        Queue notificarFalhaAutocadastroCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(notificarFalhaAutocadastroCommandQueue)
            .to(sagaExchange)
            .with(notificarFalhaAutocadastroCommandRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        jsonConverter.setClassMapper(clienteClassMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper clienteClassMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("cliente.consultar-para-aprovacao", ConsultarClienteParaAprovacaoCommand.class);
        idClassMapping.put("cliente.consultado-para-aprovacao", ClienteConsultadoParaAprovacaoEvent.class);
        idClassMapping.put("cliente.consulta-para-aprovacao.falhou", ConsultaClienteParaAprovacaoFalhouEvent.class);
        idClassMapping.put("cliente.aprovar", AprovarClienteCommand.class);
        idClassMapping.put("cliente.aprovado", ClienteAprovadoEvent.class);
        idClassMapping.put("cliente.aprovacao.falhou", AprovacaoClienteFalhouEvent.class);
        idClassMapping.put("cliente.notificar-falha-autocadastro", NotificarFalhaAutocadastroCommand.class);
        idClassMapping.put("cliente.alterar-perfil.command", AlterarPerfilClienteCommand.class);
        idClassMapping.put("cliente.perfil-alterado", ClientePerfilAlteradoEvent.class);
        idClassMapping.put("cliente.alteracao-perfil.falhou", ClienteAlteracaoFalhouEvent.class);
        idClassMapping.put("cliente.reverter-alteracao-perfil", ReverterAlteracaoPerfilClienteCommand.class);
        idClassMapping.put("cliente.perfil-revertido", ClientePerfilRevertidoEvent.class);
        idClassMapping.put("cliente.reversao-perfil.falhou", ClienteReversaoPerfilFalhouEvent.class);

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
