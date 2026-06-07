package com.ufpr.bantads.conta.application.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContaExcluidaEvent extends ContaEvent {

    private String sagaId;
    private String clienteCpf;
    private String numeroConta;
}
