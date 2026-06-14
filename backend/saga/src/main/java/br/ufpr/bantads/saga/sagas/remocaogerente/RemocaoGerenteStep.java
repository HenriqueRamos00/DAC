package br.ufpr.bantads.saga.sagas.remocaogerente;

public enum RemocaoGerenteStep {
    LISTAR_GERENTES_ATIVOS(1),
    REATRIBUIR_CONTAS(2),
    REMOVER_GERENTE(3),
    REVERTER_REATRIBUICAO_CONTAS_COMPENSACAO(4);

    private final int order;

    RemocaoGerenteStep(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }

    public String stepName() {
        return name();
    }
}