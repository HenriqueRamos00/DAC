package br.ufpr.bantads.saga.sagas.alteracaoperfil;

public enum AlteracaoPerfilStep {
    ALTERAR_PERFIL_CLIENTE(1),
    ALTERAR_USUARIO_AUTH(2),
    ALTERAR_LIMITE_CONTA(3),
    REVERTER_USUARIO_AUTH_COMPENSACAO(4),
    REVERTER_PERFIL_CLIENTE_COMPENSACAO(5);

    private final int order;

    AlteracaoPerfilStep(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }

    public String stepName() {
        return name();
    }
}
