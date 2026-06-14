package com.ufpr.bantads.conta.presentation.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ufpr.bantads.conta.application.dto.event.ContaCriadaEvent;
import com.ufpr.bantads.conta.application.dto.request.CriarContaRequest;
import com.ufpr.bantads.conta.application.dto.request.TransferenciaRequest;
import com.ufpr.bantads.conta.application.dto.request.ValorRequest;
import com.ufpr.bantads.conta.application.dto.response.ContaResponse;
import com.ufpr.bantads.conta.application.dto.response.DepositoSaqueResponse;
import com.ufpr.bantads.conta.application.dto.response.ExtratoResponse;
import com.ufpr.bantads.conta.application.dto.response.ResumoContasGerenteResponse;
import com.ufpr.bantads.conta.application.dto.response.SaldoResponse;
import com.ufpr.bantads.conta.application.dto.response.TransferenciaResponse;
import com.ufpr.bantads.conta.application.usecase.CriarContaUseCase;
import com.ufpr.bantads.conta.application.usecase.DepositarUseCase;
import com.ufpr.bantads.conta.application.usecase.GetContaByCpfUseCase;
import com.ufpr.bantads.conta.application.usecase.GetExtratoUseCase;
import com.ufpr.bantads.conta.application.usecase.GetResumoContasGerentesUseCase;
import com.ufpr.bantads.conta.application.usecase.GetSaldoUseCase;
import com.ufpr.bantads.conta.application.usecase.SacarUseCase;
import com.ufpr.bantads.conta.application.usecase.TransferenciaUseCase;
import com.ufpr.bantads.conta.domain.exception.RequisicaoInvalidaException;
import com.ufpr.bantads.conta.domain.repository.ContaQueryRepository;
import com.ufpr.bantads.conta.infrastructure.config.ContaSeedService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class ContaController {
    private final GetSaldoUseCase getSaldoUseCase;
    private final GetExtratoUseCase getExtratoUseCase;
    private final DepositarUseCase depositarUseCase;
    private final SacarUseCase sacarUseCase;
    private final TransferenciaUseCase transferenciaUseCase;
    private final GetContaByCpfUseCase contaByCpfUseCase;
    private final GetResumoContasGerentesUseCase resumoContasGerentesUseCase;
    private final CriarContaUseCase criarContaUseCase;
    private final ContaQueryRepository contaQueryRepository;
    private final ContaSeedService contaSeedService;

    @GetMapping("/reboot")
    public ResponseEntity<Void> reboot() {
        contaSeedService.reboot();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contas")
    public ResponseEntity<ContaResponse> criar(@RequestBody CriarContaRequest request) {
        ContaCriadaEvent event = criarContaUseCase.execute(request == null ? null : request.toCommand());
        return ResponseEntity.status(201).body(criarContaUseCase.toResponse(event));
    }

    @GetMapping(value = "/contas", params = "clienteCpf")
    public ResponseEntity<ContaResponse> getByClienteCpf(@RequestParam String clienteCpf) {
        ContaResponse contaResponse = contaByCpfUseCase.execute(clienteCpf);
        return ResponseEntity.ok(contaResponse);
    }

    @GetMapping(value = "/contas", params = "gerenteCpf")
    public ResponseEntity<List<ContaResponse>> getByGerenteCpf(@RequestParam String gerenteCpf) {
        List<ContaResponse> contas = contaQueryRepository.findByGerenteCpf(gerenteCpf)
                .stream()
                .map(ContaResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(contas);
    }

    @GetMapping("/contas/resumo-gerentes")
    public ResponseEntity<List<ResumoContasGerenteResponse>> getResumoContasGerentes() {
        List<ResumoContasGerenteResponse> resumos = resumoContasGerentesUseCase.execute();
        return ResponseEntity.ok(resumos);
    }

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
            @RequestBody ValorRequest request) {
        if (request == null || request.valor() == null) {
            throw new RequisicaoInvalidaException("Valor do depósito é obrigatório");
        }

        DepositoSaqueResponse depositoResponse = depositarUseCase.execute(conta, request.valor());
        return ResponseEntity.ok(depositoResponse);
    }

    @PostMapping("/contas/{conta}/sacar")
    public ResponseEntity<DepositoSaqueResponse> sacar(
            @PathVariable String conta,
            @RequestBody ValorRequest request) {
        if (request == null || request.valor() == null) {
            throw new RequisicaoInvalidaException("Valor do saque é obrigatório");
        }

        DepositoSaqueResponse depositoResponse = sacarUseCase.execute(conta, request.valor());
        return ResponseEntity.ok(depositoResponse);
    }

    @PostMapping("/contas/{conta}/transferir")
    public ResponseEntity<TransferenciaResponse> transferir(
            @PathVariable String conta,
            @RequestBody TransferenciaRequest request) {
        if (request == null || request.valor() == null) {
            throw new RequisicaoInvalidaException("Valor da transferência é obrigatório");
        }

        TransferenciaResponse transferenciaResponse = transferenciaUseCase.execute(conta, request.destino(),
                request.valor());
        return ResponseEntity.ok(transferenciaResponse);
    }
}
