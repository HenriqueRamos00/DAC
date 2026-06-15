package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.domain.exception.ContaNaoEncontradaException;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;
import com.ufpr.bantads.conta.infrastructure.messaging.publisher.ContaEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlterarLimiteContaUseCase {

    private static final BigDecimal SALARIO_MINIMO_PARA_LIMITE = new BigDecimal("2000.00");
    private static final BigDecimal METADE = new BigDecimal("0.5");

    private final ContaCommandRepository contaCommandRepository;
    private final ContaEventPublisher contaEventPublisher;

    @Transactional
    public ContaLimiteAlteradoEvent execute(AlterarLimiteContaCommand command) {
        validar(command);

        ContaCommand conta = contaCommandRepository.findByClienteCpf(command.cpf())
            .orElseThrow(() -> new ContaNaoEncontradaException("Conta não encontrada para o CPF " + command.cpf()));

        BigDecimal novoLimite = calcularLimite(command.salario(), conta.getSaldo());
        conta.setLimite(novoLimite);

        ContaCommand contaSalva = contaCommandRepository.saveAndFlush(conta);
        ContaLimiteAlteradoEvent event = ContaLimiteAlteradoEvent.fromEntity(command.sagaId(), contaSalva);

        contaEventPublisher.publishLimiteAlteradoCqrs(event);
        return event;
    }

    private void validar(AlterarLimiteContaCommand command) {
        if (command == null) {
            throw new RequisicaoInvalidaException("Dados de alteração de limite são obrigatórios");
        }

        if (command.cpf() == null || command.cpf().isBlank()) {
            throw new RequisicaoInvalidaException("CPF do cliente é obrigatório");
        }

        if (command.salario().compareTo(BigDecimal.ZERO) < 0) {
            throw new RequisicaoInvalidaException("Salário negativo é inválido");
        }
    }

    private BigDecimal calcularLimite(BigDecimal salario, BigDecimal saldoAtual) {
        BigDecimal limiteCalculado = BigDecimal.ZERO;

        if (salario != null && salario.compareTo(SALARIO_MINIMO_PARA_LIMITE) >= 0) {
            limiteCalculado = salario.multiply(METADE);
        }

        BigDecimal limiteMinimo = BigDecimal.ZERO;

        if (saldoAtual != null && saldoAtual.signum() < 0) {
            limiteMinimo = saldoAtual.abs();
        }

        BigDecimal limiteFinal = limiteCalculado.max(limiteMinimo);
        return limiteFinal.setScale(2, RoundingMode.HALF_UP);
    }
}
