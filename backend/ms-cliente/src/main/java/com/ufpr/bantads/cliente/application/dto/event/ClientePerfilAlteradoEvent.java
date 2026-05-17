package com.ufpr.bantads.cliente.application.dto.event;

import java.math.BigDecimal;

public record ClientePerfilAlteradoEvent(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String telefone,
    BigDecimal salario,
    String cep,
    String logradouro,
    String cidade,
    String estado,
    String complemento,
    String numero
) {}
