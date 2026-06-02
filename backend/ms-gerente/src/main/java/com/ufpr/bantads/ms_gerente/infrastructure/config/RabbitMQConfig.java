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
import com.ufpr.bantads.ms_gerente.application.dto.command.ListarGerentesAtivosDetalhadoCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteInseridoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosDetalhadosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.InsercaoGerenteFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosDetalhadosFalhouEvent;

// Saga Remoção de Gerente
import com.ufpr.bantads.ms_gerente.application.dto.command.ListarGerentesAtivosCommand;
import com.ufpr.bantads.ms_gerente.application.dto.command.RemoverGerenteCommand;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerentesAtivosListadosEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.ListagemGerentesAtivosFalhouEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.GerenteRemovidoEvent;
import com.ufpr.bantads.ms_gerente.application.dto.event.RemocaoGerenteFalhouEvent;

@Configuration
public class RabbitMQConfig {

    @Value("${saga.rabbitmq.exchange}")
    private String sagaExchange;

    @Value("${saga.rabbitmq.queue.gerente.inserir.command}")
    private String inserirGerenteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.command}")
    private String inserirGerenteCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.gerente.listar-ativos-detalhado.command}")
    private String listarGerentesAtivosDetalhadoCommandQueue;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos-detalhado.command}")
    private String listarGerentesAtivosDetalhadoCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.gerente.listar-ativos.command}")
    private String listarGerentesAtivosCommandQueue;

    @Value("${saga.rabbitmq.routing-key.gerente.listar-ativos.command}")
    private String listarGerentesAtivosCommandRoutingKey;

    @Value("${saga.rabbitmq.queue.gerente.remover.command}")
    private String removerGerenteCommandQueue;

    @Value("${saga.rabbitmq.routing-key.gerente.remover.command}")
    private String removerGerenteCommandRoutingKey;

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(sagaExchange);
    }

    @Bean
    public Queue inserirGerenteCommandQueue() {
        return QueueBuilder.durable(inserirGerenteCommandQueue).build();
    }

    @Bean
    public Queue listarGerentesAtivosDetalhadoCommandQueue() {
        return QueueBuilder.durable(listarGerentesAtivosDetalhadoCommandQueue).build();
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
    public Binding listarGerentesAtivosDetalhadoCommandBinding(
        Queue listarGerentesAtivosDetalhadoCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(listarGerentesAtivosDetalhadoCommandQueue)
            .to(sagaExchange)
            .with(listarGerentesAtivosDetalhadoCommandRoutingKey);
    }

    @Bean
    public Queue listarGerentesAtivosCommandQueue() {
        return QueueBuilder.durable(listarGerentesAtivosCommandQueue).build();
    }

    @Bean
    public Queue removerGerenteCommandQueue() {
        return QueueBuilder.durable(removerGerenteCommandQueue).build();
    }

    @Bean
    public Binding listarGerentesAtivosCommandBinding(
        Queue listarGerentesAtivosCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(listarGerentesAtivosCommandQueue)
            .to(sagaExchange)
            .with(listarGerentesAtivosCommandRoutingKey);
    }

    @Bean
    public Binding removerGerenteCommandBinding(
        Queue removerGerenteCommandQueue,
        TopicExchange sagaExchange
    ) {
        return BindingBuilder
            .bind(removerGerenteCommandQueue)
            .to(sagaExchange)
            .with(removerGerenteCommandRoutingKey);
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
        idClassMapping.put("gerente.listar-ativos-detalhado", ListarGerentesAtivosDetalhadoCommand.class);
        idClassMapping.put("gerente.ativos-detalhados-listados", GerentesAtivosDetalhadosListadosEvent.class);
        idClassMapping.put("gerente.listagem-ativos-detalhados.falhou", ListagemGerentesAtivosDetalhadosFalhouEvent.class);

        // Saga Remoção de Gerente
        idClassMapping.put("gerente.listar-ativos", ListarGerentesAtivosCommand.class);
        idClassMapping.put("gerente.ativos-listados", GerentesAtivosListadosEvent.class);
        idClassMapping.put("gerente.listagem-ativos.falhou", ListagemGerentesAtivosFalhouEvent.class);
        idClassMapping.put("gerente.remover", RemoverGerenteCommand.class);
        idClassMapping.put("gerente.removido", GerenteRemovidoEvent.class);
        idClassMapping.put("gerente.remocao.falhou", RemocaoGerenteFalhouEvent.class);

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
