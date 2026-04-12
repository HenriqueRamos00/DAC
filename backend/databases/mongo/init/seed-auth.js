db = db.getSiblingDB('bantads_auth');

db.usuarios.drop();

db.usuarios.insertMany([
    //CLIENTES
    {
        cpf: "12912861012",
        email: "cli1@bantads.com.br",
        senha: "tads",
        tipo: "cliente",
        ativo: true
    },
    {
        cpf: "09506382000",
        email: "cli2@bantads.com.br",
        senha: "tads",
        tipo: "cliente",
        ativo: true
    },
    {
        cpf: "85733854057",
        email: "cli3@bantads.com.br",
        senha: "tads",
        tipo: "cliente",
        ativo: true
    },
    {
        cpf: "58872160006",
        email: "cli4@bantads.com.br",
        senha: "tads",
        tipo: "cliente",
        ativo: true
    },
    {
        cpf: "76179646090",
        email: "cli5@bantads.com.br",
        senha: "tads",
        tipo: "cliente",
        ativo: true
    },

    //GERENTES
    {
        cpf: "98574307084",
        email: "ger1@bantads.com.br",
        senha: "tads",
        tipo: "gerente",
        ativo: true
    },
    {
        cpf: "64065268052",
        email: "ger2@bantads.com.br",
        senha: "tads",
        tipo: "gerente",
        ativo: true
    },
    {
        cpf: "23862179060",
        email: "ger3@bantads.com.br",
        senha: "tads",
        tipo: "gerente",
        ativo: true
    },

    //ADMINISTRADOR
    {
        cpf: "40501740066",
        email: "adm1@bantads.com.br",
        senha: "tads",
        tipo: "administrador",
        ativo: true
    }
]);

// índices para performance
db.usuarios.createIndex({ email: 1 }, { unique: true });
db.usuarios.createIndex({ cpf: 1 }, { unique: true });

print("=== BANTADS Auth: Seed concluído com sucesso! ===");
print("Total de usuários inseridos: " + db.usuarios.countDocuments());