package br.ufpr.bantads.saga.sagas.insercaogerente.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GerenteInseridoEvent {

    private String sagaId;
    private String cpf;
    private String nome;
    private String email;
    private String tipo;
}