package com.ufpr.bantads.cliente.application.usecase;

import com.ufpr.bantads.cliente.application.dto.response.ClienteResponse;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoEncontradoException;
import com.ufpr.bantads.cliente.domain.exception.ClienteNaoPendenteException;
import com.ufpr.bantads.cliente.domain.model.Cliente;
import com.ufpr.bantads.cliente.domain.model.StatusCliente;
import com.ufpr.bantads.cliente.domain.repository.ClienteRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RejeitarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public ClienteResponse execute(String cpf, String motivo) {
        Cliente cliente = executeAndReturnEntity(cpf, motivo);
        return ClienteResponse.fromEntity(cliente);
    }

    public Cliente executeAndReturnEntity(String cpf, String motivo) {
        var cliente = clienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new ClienteNaoEncontradoException(cpf));

        if (cliente.getStatus() != StatusCliente.PENDENTE) {
            throw new ClienteNaoPendenteException(cpf);
        }

        cliente.setStatus(StatusCliente.REJEITADO);
        cliente.setMotivoRejeicao(motivo);
        cliente.setDataReprovacao(LocalDateTime.now());
        cliente.setDataAprovacao(null);

        return clienteRepository.save(cliente);
    }
}
