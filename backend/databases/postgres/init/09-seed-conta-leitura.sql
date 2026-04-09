SET search_path TO schema_conta;

INSERT INTO conta_leitura (cliente_cpf, cliente_nome, cliente_email, cliente_salario, numero_conta, saldo, limite, gerente_cpf, gerente_nome, data_criacao) VALUES
    ('12912861012', 'Catharyna',   'cli1@bantads.com.br', 10000.00, '1291',     800.00,   5000.00, '98574307084', 'Geniéve',    '2000-01-01'),
    ('09506382000', 'Cleudônio',  'cli2@bantads.com.br', 20000.00, '0950', -10000.00,  10000.00, '64065268052', 'Godophredo', '1990-10-10'),
    ('85733854057', 'Catianna',    'cli3@bantads.com.br',  3000.00, '8573',  -1000.00,   1500.00, '23862179060', 'Gyândula',   '2012-12-12'),
    ('58872160006', 'Cutardo',     'cli4@bantads.com.br',   500.00, '5887',  150000.00,      0.00, '98574307084', 'Geniéve',    '2022-02-22'),
    ('76179646090', 'Coândrya',    'cli5@bantads.com.br',  1500.00, '7617',    1500.00,      0.00, '64065268052', 'Godophredo', '2025-01-01');