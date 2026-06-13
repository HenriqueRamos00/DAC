package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.CriarContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.response.ContaResponse;
import com.ufpr.bantads.conta.domain.exception.ContaJaExisteException;
import com.ufpr.bantads.conta.domain.exception.NumeroContaIndisponivelException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CriarContaUseCase {

    private static final BigDecimal SALARIO_MINIMO_PARA_LIMITE = new BigDecimal("2000.00");
    private static final BigDecimal METADE = new BigDecimal("0.5");
    private static final int MAX_TENTATIVAS_NUMERO_CONTA = 100;

    private final ContaCommandRepository contaCommandRepository;
    private final ContaEventPublisher contaEventPublisher;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public ContaCriadaEvent execute(CriarContaCommand command) {
        validar(command);

        if (contaCommandRepository.findByClienteCpf(command.clienteCpf()).isPresent()) {
            throw new ContaJaExisteException(command.clienteCpf());
        }

        ContaCommand conta = new ContaCommand();
        conta.setClienteCpf(command.clienteCpf());
        conta.setNumeroConta(gerarNumeroContaUnico());
        conta.setSaldo(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        conta.setLimite(calcularLimite(command.salario()));
        conta.setGerenteCpf(command.gerenteCpf());

        ContaCommand contaSalva = contaCommandRepository.saveAndFlush(conta);
        ContaCriadaEvent event = toEvent(command, contaSalva);
        contaEventPublisher.publishContaCriadaCqrs(event);

        return event;
    }

    public ContaResponse toResponse(ContaCriadaEvent event) {
        return new ContaResponse(
            event.getNumeroConta(),
            event.getDataCriacao().toString(),
            event.getSaldo().doubleValue(),
            event.getLimite().doubleValue(),
            event.getClienteNome(),
            event.getClienteCpf(),
            event.getGerenteCpf(),
            event.getGerenteNome(),
            event.getGerenteEmail()
        );
    }

    private void validar(CriarContaCommand command) {
        if (command == null) {
            throw new RequisicaoInvalidaException("Dados da conta são obrigatórios");
        }

        validarCampo(command.clienteCpf(), "CPF do cliente é obrigatório");
        validarCampo(command.clienteNome(), "Nome do cliente é obrigatório");
        validarCampo(command.gerenteCpf(), "CPF do gerente é obrigatório");
        validarCampo(command.gerenteNome(), "Nome do gerente é obrigatório");
        validarCampo(command.gerenteEmail(), "E-mail do gerente é obrigatório");
    }

    private void validarCampo(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RequisicaoInvalidaException(mensagem);
        }
    }

    private String gerarNumeroContaUnico() {
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS_NUMERO_CONTA; tentativa++) {
            String numeroConta = String.format("%04d", random.nextInt(10_000));

            if (contaCommandRepository.findByNumeroConta(numeroConta).isEmpty()) {
                return numeroConta;
            }
        }

        throw new NumeroContaIndisponivelException();
    }

    private BigDecimal calcularLimite(BigDecimal salario) {
        if (salario == null || salario.compareTo(SALARIO_MINIMO_PARA_LIMITE) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return salario.multiply(METADE).setScale(2, RoundingMode.HALF_UP);
    }

    private ContaCriadaEvent toEvent(CriarContaCommand command, ContaCommand conta) {
        LocalDateTime dataCriacao = conta.getDataCriacao() == null
            ? LocalDateTime.now()
            : conta.getDataCriacao();

        return ContaCriadaEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("CONTA_CRIADA")
            .eventDate(LocalDateTime.now())
            .sagaId(command.sagaId())
            .contaId(conta.getId())
            .numeroConta(conta.getNumeroConta())
            .dataCriacao(dataCriacao)
            .saldo(conta.getSaldo())
            .limite(conta.getLimite())
            .clienteCpf(command.clienteCpf())
            .clienteNome(command.clienteNome())
            .gerenteCpf(command.gerenteCpf())
            .gerenteNome(command.gerenteNome())
            .gerenteEmail(command.gerenteEmail())
            .build();
    }
}
