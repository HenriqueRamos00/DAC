package com.ufpr.bantads.auth.infrastructure.config;

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
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ufpr.bantads.auth.application.dto.command.CriarUsuarioClienteCommand;
import com.ufpr.bantads.auth.application.dto.event.CriacaoUsuarioClienteFalhouEvent;
import com.ufpr.bantads.auth.application.dto.event.UsuarioClienteCriadoEvent;

@Configuration
public class RabbitMQConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.queue.auth.criar-usuario-cliente.command}")
    private String criarUsuarioClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-cliente.command}")
    private String criarUsuarioClienteCommandRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue criarUsuarioClienteCommandQueue() {
        return QueueBuilder.durable(criarUsuarioClienteCommandQueue).build();
    }

    @Bean
    public Binding criarUsuarioClienteCommandBinding(
        Queue criarUsuarioClienteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(criarUsuarioClienteCommandQueue)
            .to(sagaExchange)
            .with(criarUsuarioClienteCommandRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(authClassMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper authClassMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("auth.criar-usuario-cliente.command", CriarUsuarioClienteCommand.class);
        idClassMapping.put("auth.usuario-cliente-criado", UsuarioClienteCriadoEvent.class);
        idClassMapping.put("auth.criacao-usuario-cliente.falhou", CriacaoUsuarioClienteFalhouEvent.class);

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
