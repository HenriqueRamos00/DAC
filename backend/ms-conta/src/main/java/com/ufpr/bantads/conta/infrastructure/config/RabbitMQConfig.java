package com.ufpr.bantads.conta.infrastructure.config;

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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;

@Configuration
public class RabbitMQConfig {

    @Value("${cqrs.rabbitmq.exchange}")
    private String exchange;

    @Value("${cqrs.rabbitmq.queue}")
    private String queue;

    @Value("${cqrs.rabbitmq.routing-key}")
    private String routingKey;

    @Value("${saga.rabbitmq.exchange}")
    private String sagaExchange;

    @Value("${saga.rabbitmq.queue.conta.alterar-limite.command}")
    private String alterarLimiteContaCommandQueue;

    @Value("${saga.rabbitmq.routing-key.conta.alterar-limite.command}")
    private String alterarLimiteContaCommandRoutingKey;

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(sagaExchange);
    }

    @Bean
    public Queue productQueryQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Queue alterarLimiteContaCommandQueue() {
        return QueueBuilder.durable(alterarLimiteContaCommandQueue).build();
    }

    @Bean
    public Binding binding(
        @Qualifier("productQueryQueue") Queue productQueryQueue,
        @Qualifier("productExchange") TopicExchange productExchange
    ) {
        return BindingBuilder
            .bind(productQueryQueue)
            .to(productExchange)
            .with(routingKey);
    }

    @Bean
    public Binding alterarLimiteContaCommandBinding(
        @Qualifier("alterarLimiteContaCommandQueue") Queue alterarLimiteContaCommandQueue,
        @Qualifier("sagaExchange") TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(alterarLimiteContaCommandQueue)
            .to(sagaExchange)
            .with(alterarLimiteContaCommandRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter jsonConverter = new JacksonJsonMessageConverter();
        jsonConverter.setClassMapper(contaClassMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper contaClassMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("conta.alterar-limite.command", AlterarLimiteContaCommand.class);
        idClassMapping.put("conta.limite-alterado", ContaLimiteAlteradoEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
