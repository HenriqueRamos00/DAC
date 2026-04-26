package com.ufpr.bantads.cliente.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.queue.cliente.aprovar.command}")
    private String aprovarClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.aprovar.command}")
    private String aprovarClienteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.cliente.rejeitar.command}")
    private String rejeitarClienteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.cliente.rejeitar.command}")
    private String rejeitarClienteCommandRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue aprovarClienteCommandQueue() {
        return QueueBuilder.durable(aprovarClienteCommandQueue).build();
    }

    @Bean
    public Queue rejeitarClienteCommandQueue() {
        return QueueBuilder.durable(rejeitarClienteCommandQueue).build();
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
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
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
