package br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.command;

import br.ufpr.bantads.saga.sagas.alteracaoperfil.dto.event.ClientePerfilAlteradoEvent;

public record AlterarUsuarioClienteAuthCommand(
    String sagaId,
    String cpf,
    String nome,
    String email,
    String nomeAnterior,
    String emailAnterior
) {
    public static AlterarUsuarioClienteAuthCommand fromEvent(ClientePerfilAlteradoEvent event) {
        return new AlterarUsuarioClienteAuthCommand(
            event.getSagaId(),
            event.getCpf(),
            event.getNome(),
            event.getEmail(),
            event.getNomeAnterior(),
            event.getEmailAnterior()
        );
    }
}
