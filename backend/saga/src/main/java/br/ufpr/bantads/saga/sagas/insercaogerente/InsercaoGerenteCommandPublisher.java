package br.ufpr.bantads.saga.sagas.insercaogerente;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.AtribuirGerenteContaCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ConsultarGerenteMaisContasCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.CriarUsuarioGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.ExcluirUsuarioGerenteCompensacaoCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.InserirGerenteCommand;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.command.RemoverGerenteCompensacaoCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class InsercaoGerenteCommandPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${saga.rabbitmq.exchange}")
    private String exchange;

    @Value("${saga.rabbitmq.routing-key.conta.consultar-gerente-mais-contas.command}")
    private String consultarGerenteMaisContasRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.inserir.command}")
    private String inserirGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.conta.atribuir-gerente.command}")
    private String atribuirGerenteContaRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.criar-usuario-gerente.command}")
    private String criarUsuarioGerenteRoutingKey;

    @Value("${saga.rabbitmq.routing-key.gerente.remover-compensacao.command}")
    private String removerGerenteCompensacaoRoutingKey;

    @Value("${saga.rabbitmq.routing-key.auth.excluir-usuario-gerente-compensacao.command}")
    private String excluirUsuarioGerenteCompensacaoRoutingKey;

    public void publishConsultarGerenteMaisContas(ConsultarGerenteMaisContasCommand command) {
        log.info("Publicando comando consultar-gerente-mais-contas: {}", command);
        rabbitTemplate.convertAndSend(exchange, consultarGerenteMaisContasRoutingKey, command);
    }

    public void publishInserirGerente(InserirGerenteCommand command) {
        log.info("Publicando comando inserir-gerente: {}", command);
        rabbitTemplate.convertAndSend(exchange, inserirGerenteRoutingKey, command);
    }

    public void publishAtribuirGerenteConta(AtribuirGerenteContaCommand command) {
        log.info("Publicando comando atribuir-gerente-conta: {}", command);
        rabbitTemplate.convertAndSend(exchange, atribuirGerenteContaRoutingKey, command);
    }

    public void publishCriarUsuarioGerente(CriarUsuarioGerenteCommand command) {
        log.info("Publicando comando criar-usuario-gerente saga {} cpf {}", command.getSagaId(), command.getCpf());
        rabbitTemplate.convertAndSend(exchange, criarUsuarioGerenteRoutingKey, command);
    }

    public void publishRemoverGerenteCompensacao(RemoverGerenteCompensacaoCommand command) {
        log.info("Publicando comando remover-gerente-compensacao saga {} cpf {}", command.getSagaId(), command.getCpf());
        rabbitTemplate.convertAndSend(exchange, removerGerenteCompensacaoRoutingKey, command);
    }

    public void publishExcluirUsuarioGerenteCompensacao(ExcluirUsuarioGerenteCompensacaoCommand command) {
        log.info("Publicando comando excluir-usuario-gerente-compensacao saga {} cpf {}", command.getSagaId(), command.getCpf());
        rabbitTemplate.convertAndSend(exchange, excluirUsuarioGerenteCompensacaoRoutingKey, command);
    }
}