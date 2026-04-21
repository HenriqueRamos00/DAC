package com.ufpr.bantads.conta.presentation.rest;

import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.conta.application.dto.request.ValorRequest;
import com.ufpr.bantads.conta.application.dto.response.DepositoSaqueResponse;
import com.ufpr.bantads.conta.application.dto.response.ExtratoResponse;
import com.ufpr.bantads.conta.application.dto.response.SaldoResponse;
import com.ufpr.bantads.conta.application.usecase.DepositarUseCase;
import com.ufpr.bantads.conta.application.usecase.GetExtratoUseCase;
import com.ufpr.bantads.conta.application.usecase.GetSaldoUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
public class ContaController {

    private final GetSaldoUseCase getSaldoUseCase;
    private final GetExtratoUseCase getExtratoUseCase;
    private final DepositarUseCase depositarUseCase;

    @GetMapping("/contas/{conta}/saldo")
    public ResponseEntity<SaldoResponse> getSaldo(@PathVariable String conta) {
        SaldoResponse saldoResponse = getSaldoUseCase.execute(conta);
        return ResponseEntity.ok(saldoResponse);
    }
    
    @GetMapping("/contas/{conta}/extrato")
    public ResponseEntity<ExtratoResponse> getExtrato(@PathVariable String conta) {
        ExtratoResponse extratoResponse = getExtratoUseCase.getExtrato(conta);
        return ResponseEntity.ok(extratoResponse);
    }

    @PostMapping("/contas/{conta}/depositar")
    public ResponseEntity<DepositoSaqueResponse> depositar(
        @PathVariable String conta,
        @RequestBody ValorRequest request
    ) {
        if (request == null || request.valor() == null) {
            return ResponseEntity.badRequest().build();
        }

        DepositoSaqueResponse depositoResponse = depositarUseCase.execute(conta, request.valor());
        return ResponseEntity.ok(depositoResponse);
    }
    
}
