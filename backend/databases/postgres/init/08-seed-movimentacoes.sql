INSERT INTO conta_write.movimentacao (conta_id, data_hora, tipo, conta_destino_id, valor) VALUES
    (1, '2020-01-01 10:00:00', 'DEPOSITO',      NULL, 1000.00),
    (1, '2020-01-01 11:00:00', 'DEPOSITO',      NULL,  900.00),
    (1, '2020-01-01 12:00:00', 'SAQUE',         NULL,  550.00),
    (1, '2020-01-01 13:00:00', 'SAQUE',         NULL,  350.00),
    (1, '2020-01-10 15:00:00', 'DEPOSITO',      NULL, 2000.00),
    (1, '2020-01-15 08:00:00', 'SAQUE',         NULL,  500.00),
    (1, '2020-01-20 12:00:00', 'TRANSFERENCIA', 2,    1700.00);

INSERT INTO conta_write.movimentacao (conta_id, data_hora, tipo, conta_destino_id, valor) VALUES
    (2, '2025-01-01 12:00:00', 'DEPOSITO', NULL, 1000.00),
    (2, '2025-01-02 10:00:00', 'DEPOSITO', NULL, 5000.00),
    (2, '2025-01-10 10:00:00', 'SAQUE',    NULL,  200.00),
    (2, '2025-02-05 10:00:00', 'DEPOSITO', NULL, 7000.00);

INSERT INTO conta_write.movimentacao (conta_id, data_hora, tipo, conta_destino_id, valor) VALUES
    (3, '2025-05-05 00:00:00', 'DEPOSITO', NULL, 1000.00),
    (3, '2025-05-06 00:00:00', 'SAQUE',    NULL, 2000.00);

INSERT INTO conta_write.movimentacao (conta_id, data_hora, tipo, conta_destino_id, valor) VALUES
    (4, '2025-06-01 00:00:00', 'DEPOSITO', NULL, 150000.00);

INSERT INTO conta_write.movimentacao (conta_id, data_hora, tipo, conta_destino_id, valor) VALUES
    (5, '2025-07-01 00:00:00', 'DEPOSITO', NULL, 1500.00);