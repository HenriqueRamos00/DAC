package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.command.AlterarLimiteContaCommand;
import com.ufpr.bantads.conta.application.dto.event.ContaLimiteAlteradoEvent;
import com.ufpr.bantads.conta.domain.model.entity.ContaCommand;
import com.ufpr.bantads.conta.domain.repository.ContaCommandRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AlterarLimiteUseCase {

    private final ContaCommandRepository commandRepository;
    private final BigDecimal salarioCutoff = new BigDecimal(2000);

    public ContaLimiteAlteradoEvent execute(AlterarLimiteContaCommand command) {

        ContaCommand conta = commandRepository.findByClienteCpf(command.cpf()).orElseThrow(
            () -> new IllegalArgumentException("Conta não encontrada")
        );

        BigDecimal newSalario = command.salario();

        BigDecimal newLimite = new BigDecimal(0);
        if (newSalario.compareTo(salarioCutoff) > 0) {
            newLimite = command.salario().divide(
                new BigDecimal(2), RoundingMode.HALF_UP);
        }
        if (newLimite.compareTo(conta.getSaldo().negate()) < 0) {
            newLimite = conta.getSaldo().negate();
        }

        conta.setLimite(newLimite);
        ContaCommand atualizado = commandRepository.save(conta);

        return ContaLimiteAlteradoEvent.fromEntity(command.sagaId(), atualizado);
    }

}
