SET search_path TO schema_conta;

-- banco de comando (escrita)

CREATE TABLE conta (
    id              BIGSERIAL       PRIMARY KEY,
    cliente_cpf     VARCHAR(11)     NOT NULL UNIQUE,
    numero_conta    VARCHAR(4)      NOT NULL UNIQUE,
    data_criacao    DATE            NOT NULL,
    saldo           DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    limite          DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    gerente_cpf     VARCHAR(11)     NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movimentacao (
    id              BIGSERIAL       PRIMARY KEY,
    conta_id        BIGINT          NOT NULL REFERENCES conta(id),
    data_hora       TIMESTAMP       NOT NULL,
    tipo            VARCHAR(20)     NOT NULL
                        CHECK (tipo IN ('deposito', 'saque', 'transferencia')),
    cliente_origem  VARCHAR(11),
    cliente_destino VARCHAR(11),
    valor           DECIMAL(12,2)   NOT NULL CHECK (valor > 0),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- índices para consultas
CREATE INDEX idx_movimentacao_conta ON movimentacao(conta_id);
CREATE INDEX idx_movimentacao_data ON movimentacao(data_hora);
CREATE INDEX idx_conta_gerente ON conta(gerente_cpf);
CREATE INDEX idx_conta_cliente ON conta(cliente_cpf);

-- banco de consulta (leitura) — desnormalizado

CREATE TABLE conta_leitura (
    id              BIGSERIAL       PRIMARY KEY,
    cliente_cpf     VARCHAR(11)     NOT NULL,
    cliente_nome    VARCHAR(100),
    cliente_email   VARCHAR(100),
    cliente_salario DECIMAL(12,2),
    numero_conta    VARCHAR(4)      NOT NULL,
    data_criacao    DATE            NOT NULL,
    saldo           DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    limite          DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    gerente_cpf     VARCHAR(11)     NOT NULL,
    gerente_nome    VARCHAR(100),
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conta_leitura_cliente ON conta_leitura(cliente_cpf);
CREATE INDEX idx_conta_leitura_gerente ON conta_leitura(gerente_cpf);