package com.ufpr.bantads.conta.infrastructure.config;

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

    @Value("${cqrs.rabbitmq.exchange}")
    private String exchange;

    @Value("${cqrs.rabbitmq.queue}")
    private String queue;

    @Value("${cqrs.rabbitmq.routing-key}")
    private String routingKey;

    @Bean
    public TopicExchange productExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue productQueryQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding binding(Queue productQueryQueue, TopicExchange productExchange) {
        return BindingBuilder
            .bind(productQueryQueue)
            .to(productExchange)
            .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
