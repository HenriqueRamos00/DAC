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
    private String nomeAnterior;
    private String emailAnterior;
    private String telefoneAnterior;
    private BigDecimal salarioAnterior;
    private String cepAnterior;
    private String logradouroAnterior;
    private String cidadeAnterior;
    private String estadoAnterior;
    private String complementoAnterior;
    private String numeroAnterior;

}
