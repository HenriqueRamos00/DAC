SET search_path TO schema_cliente;

CREATE TABLE endereco (
    id              BIGSERIAL       PRIMARY KEY,
    logradouro      VARCHAR(200)    NOT NULL,
    numero          VARCHAR(10)     NOT NULL,
    complemento     VARCHAR(100),
    cep             VARCHAR(8)      NOT NULL,
    cidade          VARCHAR(100)    NOT NULL,
    estado          VARCHAR(2)      NOT NULL
);

CREATE TABLE cliente (
    id              BIGSERIAL       PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    cpf             VARCHAR(11)     NOT NULL UNIQUE,
    telefone        VARCHAR(15)     UNIQUE,
    salario         DECIMAL(12,2)   NOT NULL,
    endereco_id     BIGINT          NOT NULL UNIQUE REFERENCES endereco(id),
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDENTE',   --PENDENTE, APROVADO, REJEITADO
    motivo_rejeicao TEXT,
    data_aprovacao  TIMESTAMP,
    data_reprovacao TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- índice para busca por CPF para usar validação de duplicidade no autocadastro
CREATE INDEX idx_cliente_cpf ON cliente(cpf);
