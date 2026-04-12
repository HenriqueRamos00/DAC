SET search_path TO schema_conta;

INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem, cliente_destino, valor) VALUES
    (1, '2020-01-01 10:00:00', 'deposito',      '12912861012', NULL,          1000.00),
    (1, '2020-01-01 11:00:00', 'deposito',      '12912861012', NULL,           900.00),
    (1, '2020-01-01 12:00:00', 'saque',         '12912861012', NULL,           550.00),
    (1, '2020-01-01 13:00:00', 'saque',         '12912861012', NULL,           350.00),
    (1, '2020-01-10 15:00:00', 'deposito',      '12912861012', NULL,          2000.00),
    (1, '2020-01-15 08:00:00', 'saque',         '12912861012', NULL,           500.00),
    (1, '2020-01-20 12:00:00', 'transferencia', '12912861012', '09506382000', 1700.00);

INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem, cliente_destino, valor) VALUES
    (2, '2025-01-01 12:00:00', 'deposito',      '09506382000', NULL,          1000.00),
    (2, '2025-01-02 10:00:00', 'deposito',      '09506382000', NULL,          5000.00),
    (2, '2025-01-10 10:00:00', 'saque',         '09506382000', NULL,           200.00),
    (2, '2025-02-05 10:00:00', 'deposito',      '09506382000', NULL,          7000.00);

INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem, cliente_destino, valor) VALUES
    (3, '2025-05-05 00:00:00', 'deposito',      '85733854057', NULL,          1000.00),
    (3, '2025-05-06 00:00:00', 'saque',         '85733854057', NULL,          2000.00);

INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem, cliente_destino, valor) VALUES
    (4, '2025-06-01 00:00:00', 'deposito',      '58872160006', NULL,        150000.00);

INSERT INTO movimentacao (conta_id, data_hora, tipo, cliente_origem, cliente_destino, valor) VALUES
    (5, '2025-07-01 00:00:00', 'deposito',      '76179646090', NULL,          1500.00);