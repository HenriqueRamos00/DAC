package br.ufpr.bantads.saga.sagas.aprovacaocliente;

public enum AprovacaoClienteStep {
    CONSULTAR_CLIENTE(1),
    CRIAR_USUARIO_CLIENTE(2),
    LISTAR_GERENTES_ATIVOS(3),
    SELECIONAR_GERENTE_PARA_NOVA_CONTA(4),
    CRIAR_CONTA(5),
    APROVAR_CLIENTE(6),
    EXCLUIR_CONTA_CLIENTE(7);

    private final int order;

    AprovacaoClienteStep(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }

    public String stepName() {
        return name();
    }
}
