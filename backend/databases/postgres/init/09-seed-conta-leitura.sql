INSERT INTO conta_read.conta_view (
    id,
    numero_conta,
    data_criacao,
    saldo,
    limite,
    cliente_nome,
    cliente_cpf,
    gerente_cpf,
    gerente_nome
) VALUES
    (1, '1291', '2000-01-01 00:00:00',    800.00,   5000.00, 'Catharyna', '12912861012', '98574307084', 'Geniéve'),
    (2, '0950', '1990-10-10 00:00:00', -10000.00,  10000.00, 'Cleudônio', '09506382000', '64065268052', 'Godophredo'),
    (3, '8573', '2012-12-12 00:00:00',  -1000.00,   1500.00, 'Catianna',  '85733854057', '23862179060', 'Gyândula'),
    (4, '5887', '2022-02-22 00:00:00', 150000.00,      0.00, 'Cutardo',   '58872160006', '98574307084', 'Geniéve'),
    (5, '7617', '2025-01-01 00:00:00',   1500.00,      0.00, 'Coândrya',  '76179646090', '64065268052', 'Godophredo');

INSERT INTO conta_read.movimentacao_view (
    id,
    data_hora,
    tipo,
    valor,
    conta_origem_numero,
    cliente_origem_nome,
    conta_destino_numero,
    cliente_destino_nome
) VALUES
    (1,  '2020-01-01 10:00:00', 'DEPOSITO',      1000.00, '1291', 'Catharyna', NULL,   NULL),
    (2,  '2020-01-01 11:00:00', 'DEPOSITO',       900.00, '1291', 'Catharyna', NULL,   NULL),
    (3,  '2020-01-01 12:00:00', 'SAQUE',          550.00, '1291', 'Catharyna', NULL,   NULL),
    (4,  '2020-01-01 13:00:00', 'SAQUE',          350.00, '1291', 'Catharyna', NULL,   NULL),
    (5,  '2020-01-10 15:00:00', 'DEPOSITO',      2000.00, '1291', 'Catharyna', NULL,   NULL),
    (6,  '2020-01-15 08:00:00', 'SAQUE',          500.00, '1291', 'Catharyna', NULL,   NULL),
    (7,  '2020-01-20 12:00:00', 'TRANSFERENCIA', 1700.00, '1291', 'Catharyna', '0950', 'Cleudônio'),
    (8,  '2025-01-01 12:00:00', 'DEPOSITO',      1000.00, '0950', 'Cleudônio', NULL,   NULL),
    (9,  '2025-01-02 10:00:00', 'DEPOSITO',      5000.00, '0950', 'Cleudônio', NULL,   NULL),
    (10, '2025-01-10 10:00:00', 'SAQUE',          200.00, '0950', 'Cleudônio', NULL,   NULL),
    (11, '2025-02-05 10:00:00', 'DEPOSITO',      7000.00, '0950', 'Cleudônio', NULL,   NULL),
    (12, '2025-05-05 00:00:00', 'DEPOSITO',      1000.00, '8573', 'Catianna',  NULL,   NULL),
    (13, '2025-05-06 00:00:00', 'SAQUE',         2000.00, '8573', 'Catianna',  NULL,   NULL),
    (14, '2025-06-01 00:00:00', 'DEPOSITO',    150000.00, '5887', 'Cutardo',   NULL,   NULL),
    (15, '2025-07-01 00:00:00', 'DEPOSITO',      1500.00, '7617', 'Coândrya',  NULL,   NULL);