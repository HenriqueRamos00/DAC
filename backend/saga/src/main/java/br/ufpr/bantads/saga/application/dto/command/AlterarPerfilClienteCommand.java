package br.ufpr.bantads.saga.application.dto.command;

import java.math.BigDecimal;

import br.ufpr.bantads.saga.application.dto.request.AlterarPerfilRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlterarPerfilClienteCommand {
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

    public static AlterarPerfilClienteCommand fromRequest(
        String cpf,
        String sagaId, 
        AlterarPerfilRequest request) 
        {
        return new AlterarPerfilClienteCommand(
            sagaId, 
            cpf,
            request.nome(), 
            request.email(), 
            null,
            request.salario(), 
            request.cep(), 
            request.endereco(), 
            request.cidade(), 
            request.estado(), 
            null, 
            null
        );
    }
}
