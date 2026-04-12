SET search_path TO schema_cliente;

INSERT INTO cliente (nome, email, cpf, salario, logradouro, numero, complemento, cep, cidade, estado, status, data_aprovacao) VALUES
    ('Catharyna',   'cli1@bantads.com.br', '12912861012', 10000.00,
        'Rua XV de Novembro',  '100', 'Apto 101', '80020310', 'Curitiba', 'PR',
        'APROVADO', '2000-01-01 00:00:00'),
    ('Cleudônio',  'cli2@bantads.com.br', '09506382000', 20000.00,
        'Av. Sete de Setembro', '200', NULL,       '80040120', 'Curitiba', 'PR',
        'APROVADO', '1990-10-10 00:00:00'),
    ('Catianna',    'cli3@bantads.com.br', '85733854057',  3000.00,
        'Rua Marechal Deodoro', '300', 'Sala 5',  '80010010', 'Curitiba', 'PR',
        'APROVADO', '2012-12-12 00:00:00'),
    ('Cutardo',     'cli4@bantads.com.br', '58872160006',   500.00,
        'Rua Barão do Rio Branco', '400', NULL,    '80010180', 'Curitiba', 'PR',
        'APROVADO', '2022-02-22 00:00:00'),
    ('Coândrya',    'cli5@bantads.com.br', '76179646090',  1500.00,
        'Rua Comendador Araújo', '500', 'Bloco B', '80420000', 'Curitiba', 'PR',
        'APROVADO', '2025-01-01 00:00:00');