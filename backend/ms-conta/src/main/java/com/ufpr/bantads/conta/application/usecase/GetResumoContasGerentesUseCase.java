package com.ufpr.bantads.conta.application.usecase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ufpr.bantads.conta.application.dto.response.ContaResponse;
import com.ufpr.bantads.conta.application.dto.response.ResumoContasGerenteResponse;
import com.ufpr.bantads.conta.domain.model.entity.ContaQuery;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetResumoContasGerentesUseCase {

    private final ContaQueryRepository contaQueryRepository;

    public List<ResumoContasGerenteResponse> execute() {
        List<ContaQuery> contas = contaQueryRepository.findAll();
        Map<String, List<ContaQuery>> contasPorGerente = agruparPorGerente(contas);
        List<ResumoContasGerenteResponse> resumos = new ArrayList<>();

        for (String gerenteCpf : contasPorGerente.keySet()) {
            resumos.add(toResumo(gerenteCpf, contasPorGerente.get(gerenteCpf)));
        }

        return resumos;
    }

    private Map<String, List<ContaQuery>> agruparPorGerente(List<ContaQuery> contas) {
        Map<String, List<ContaQuery>> contasPorGerente = new HashMap<>();

        for (ContaQuery conta : contas) {
            String gerenteCpf = conta.getGerenteCpf();

            if (!contasPorGerente.containsKey(gerenteCpf)) {
                contasPorGerente.put(gerenteCpf, new ArrayList<>());
            }

            contasPorGerente.get(gerenteCpf).add(conta);
        }

        return contasPorGerente;
    }

    private ResumoContasGerenteResponse toResumo(String gerenteCpf, List<ContaQuery> contas) {
        return new ResumoContasGerenteResponse(
            gerenteCpf,
            contas.stream().map(ContaResponse::fromEntity).toList(),
            somarSaldos(contas, true),
            somarSaldos(contas, false)
        );
    }

    private BigDecimal somarSaldos(List<ContaQuery> contas, boolean positivos) {
        return contas.stream()
            .map(ContaQuery::getSaldo)
            .filter(saldo -> positivos ? saldo.signum() >= 0 : saldo.signum() < 0)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
