package com.ufpr.bantads.ms_gerente.infrastructure.config;

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

import com.ufpr.bantads.ms_gerente.application.dto.command.InserirGerenteCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteInseridoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.InsercaoGerenteFalhouEvent;

@Configuration
public class RabbitMQConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String sagaExchange;

    @Value("${saga.rabbitmq.queue.gerente.inserir.command}")
    private String inserirGerenteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.command}")
    private String inserirGerenteCommandRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(sagaExchange);
    }

    @Bean
    public Queue inserirGerenteCommandQueue() {
        return QueueBuilder.durable(inserirGerenteCommandQueue).build();
    }

    @Bean
    public Binding inserirGerenteCommandBinding(
        Queue inserirGerenteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(inserirGerenteCommandQueue)
            .to(sagaExchange)
            .with(inserirGerenteCommandRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(gerenteClassMapper());
        return jsonConverter;
    }

    @Bean
    public DefaultClassMapper gerenteClassMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("gerente.inserir", InserirGerenteCommand.class);
        idClassMapping.put("gerente.inserido", GerenteInseridoEvent.class);
        idClassMapping.put("gerente.insercao.falhou", InsercaoGerenteFalhouEvent.class);

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