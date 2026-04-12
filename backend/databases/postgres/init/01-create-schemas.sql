CREATE SCHEMA IF NOT EXISTS schema_cliente;
CREATE SCHEMA IF NOT EXISTS schema_conta;
CREATE SCHEMA IF NOT EXISTS schema_gerente;

GRANT ALL PRIVILEGES ON SCHEMA schema_cliente TO postgres_user;
GRANT ALL PRIVILEGES ON SCHEMA schema_conta TO postgres_user;
GRANT ALL PRIVILEGES ON SCHEMA schema_gerente TO postgres_user;