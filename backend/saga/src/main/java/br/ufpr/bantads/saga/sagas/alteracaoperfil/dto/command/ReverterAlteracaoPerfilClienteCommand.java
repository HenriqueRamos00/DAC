package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command;

import java.math.BigDecimal;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReverterAlteracaoPerfilClienteCommand {
    private String sagaId;
    private String cpf;
    private String nome;
    private String email;
    private String telefone;
    private BigDecimal salario;
    private String cep;
    private String logradouro;
    private String cidade;
    private String estado;
    private String complemento;
    private String numero;

    public static ReverterAlteracaoPerfilClienteCommand fromEvent(ClientePerfilAlteradoEvent event) {
        return new ReverterAlteracaoPerfilClienteCommand(
            event.getSagaId(),
            event.getCpf(),
            event.getNomeAnterior(),
            event.getEmailAnterior(),
            event.getTelefoneAnterior(),
            event.getSalarioAnterior(),
            event.getCepAnterior(),
            event.getLogradouroAnterior(),
            event.getCidadeAnterior(),
            event.getEstadoAnterior(),
            event.getComplementoAnterior(),
            event.getNumeroAnterior()
        );
    }
}
