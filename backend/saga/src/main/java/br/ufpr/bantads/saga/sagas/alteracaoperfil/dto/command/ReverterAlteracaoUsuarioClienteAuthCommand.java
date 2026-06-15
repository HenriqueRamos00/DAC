package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.UsuarioClienteAuthAlteradoEvent;

public record ReverterAlteracaoUsuarioClienteAuthCommand(
    String sagaId,
    String cpf,
    String nome,
    String email
) {
    public static ReverterAlteracaoUsuarioClienteAuthCommand fromEvent(UsuarioClienteAuthAlteradoEvent event) {
        return new ReverterAlteracaoUsuarioClienteAuthCommand(
            event.sagaId(),
            event.cpf(),
            event.nomeAnterior(),
            event.emailAnterior()
        );
    }
}
