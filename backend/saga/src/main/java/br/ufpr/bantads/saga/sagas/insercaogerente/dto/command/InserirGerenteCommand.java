package br.ufpr.bantads.saga.sagas.insercaogerente.dto.command;

import br.ufpr.bantads.saga.sagas.insercaogerente.dto.request.InserirGerenteRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InserirGerenteCommand {

    private String sagaId;
    private String cpf;
    private String nome;
    private String email;
    private String senha;
    private String tipo;

    public static InserirGerenteCommand fromRequest(String sagaId, InserirGerenteRequest request) {
        return new InserirGerenteCommand(
            sagaId,
            request.cpf(),
            request.nome(),
            request.email(),
            request.senha(),
            request.tipo()
        );
    }
}