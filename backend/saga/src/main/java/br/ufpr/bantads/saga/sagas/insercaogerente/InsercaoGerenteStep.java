package br.ufpr.bantads.saga.sagas.insercaogerente;

public enum InsercaoGerenteStep {
    REQUEST_INICIAL(0),
    CONSULTAR_GERENTE_MAIS_CONTAS(1),
    INSERIR_GERENTE(2),
    CRIAR_USUARIO_GERENTE(3),
    ATRIBUIR_GERENTE_CONTA(4),
    EXCLUIR_USUARIO_GERENTE_COMPENSACAO(5),
    REMOVER_GERENTE_COMPENSACAO(6);

    private final int order;

    InsercaoGerenteStep(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }

    public String stepName() {
        return name();
    }
}