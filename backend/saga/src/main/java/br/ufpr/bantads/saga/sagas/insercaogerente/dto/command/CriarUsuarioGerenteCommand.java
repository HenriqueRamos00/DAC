package br.ufpr.bantads.saga.sagas.insercaogerente.dto.command;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.event.GerenteInseridoEvent;
import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriarUsuarioGerenteCommand {

    private String sagaId;
    private String cpf;
    private String email;
    private String senha;

    public static CriarUsuarioGerenteCommand fromGerenteInserido(GerenteInseridoEvent event, InserirGerenteRequest request) {
        return new CriarUsuarioGerenteCommand(
            event.getSagaId(),
            event.getCpf(),
            event.getEmail(),
            request.senha()
        );
    }
}