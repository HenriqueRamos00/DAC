CREATE TABLE conta_write.conta (
    id               BIGSERIAL      PRIMARY KEY,
    cliente_cpf      VARCHAR(11)    NOT NULL,
    numero_conta     VARCHAR(20)    NOT NULL UNIQUE,
    data_criacao     TIMESTAMP      NOT NULL DEFAULT NOW(),
    saldo            DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
    limite           DECIMAL(15,2)  NOT NULL DEFAULT 0.00,
    gerente_cpf      VARCHAR(11)    NOT NULL
);

CREATE TABLE conta_write.movimentacao (
    id                BIGSERIAL      PRIMARY KEY,
    conta_id          BIGINT         NOT NULL REFERENCES conta_write.conta(id),
    data_hora         TIMESTAMP      NOT NULL DEFAULT NOW(),
    tipo              VARCHAR(15)    NOT NULL
                        CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')),
    conta_destino_id  BIGINT         NULL REFERENCES conta_write.conta(id),
    valor             DECIMAL(15,2)  NOT NULL
);

CREATE INDEX idx_conta_write_cliente_cpf ON conta_write.conta(cliente_cpf);
CREATE INDEX idx_conta_write_gerente_cpf ON conta_write.conta(gerente_cpf);
CREATE INDEX idx_movimentacao_write_conta_id ON conta_write.movimentacao(conta_id);
CREATE INDEX idx_movimentacao_write_data_hora ON conta_write.movimentacao(data_hora);
CREATE INDEX idx_movimentacao_write_conta_destino_id ON conta_write.movimentacao(conta_destino_id);

CREATE TABLE conta_read.conta_view (
    id              BIGINT        PRIMARY KEY,
    numero_conta    VARCHAR(20)   NOT NULL,
    data_criacao    TIMESTAMP     NOT NULL,
    saldo           DECIMAL(15,2) NOT NULL,
    limite          DECIMAL(15,2) NOT NULL,
    cliente_nome    VARCHAR(150)  NOT NULL,
    cliente_cpf     VARCHAR(11)   NOT NULL,
    gerente_cpf     VARCHAR(11)   NOT NULL,
    gerente_nome    VARCHAR(150)  NOT NULL
);

CREATE TABLE conta_read.movimentacao_view (
    id                    BIGINT        PRIMARY KEY,
    data_hora             TIMESTAMP     NOT NULL,
    tipo                  VARCHAR(15)   NOT NULL,
    valor                 DECIMAL(15,2) NOT NULL,
    conta_origem_numero   VARCHAR(20)   NOT NULL,
    cliente_origem_nome   VARCHAR(150)  NOT NULL,
    conta_destino_numero  VARCHAR(20)   NULL,
    cliente_destino_nome  VARCHAR(150)  NULL
);

CREATE INDEX idx_conta_read_conta_view_cliente_cpf ON conta_read.conta_view(cliente_cpf);
CREATE INDEX idx_conta_read_conta_view_gerente_cpf ON conta_read.conta_view(gerente_cpf);
CREATE INDEX idx_conta_read_movimentacao_view_data_hora ON conta_read.movimentacao_view(data_hora);
CREATE INDEX idx_conta_read_movimentacao_view_tipo ON conta_read.movimentacao_view(tipo);