SET search_path TO schema_gerente;

CREATE TABLE gerente (
    id          BIGSERIAL       PRIMARY KEY,
    nome        VARCHAR(100)    NOT NULL,
    email       VARCHAR(100)    NOT NULL UNIQUE,
    cpf         VARCHAR(11)     NOT NULL UNIQUE,
    telefone    VARCHAR(15),
    created_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);