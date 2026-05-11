package br.ufpr.bantads.saga.config;

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
