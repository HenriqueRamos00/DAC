CREATE SCHEMA IF NOT EXISTS schema_cliente;
CREATE SCHEMA IF NOT EXISTS schema_gerente;
CREATE SCHEMA IF NOT EXISTS conta_write;
CREATE SCHEMA IF NOT EXISTS conta_read;

GRANT ALL PRIVILEGES ON SCHEMA schema_cliente TO postgres_user;
GRANT ALL PRIVILEGES ON SCHEMA schema_gerente TO postgres_user;
GRANT ALL PRIVILEGES ON SCHEMA conta_write TO postgres_user;
GRANT ALL PRIVILEGES ON SCHEMA conta_read TO postgres_user;