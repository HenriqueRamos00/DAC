package br.ufpr.bantads.saga.sagas.aprovacaocliente.dto.command;

//validar para não permitir isso,
// senha trafegando nos MS, ms-auth poderia ser
// resposável pelo envio de email com senha do usuário
public record AprovarClienteCommand(
    String sagaId,
    String cpf,
    String senhaGerada
) {}
