package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientePerfilAlteradoEvent {

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

}
