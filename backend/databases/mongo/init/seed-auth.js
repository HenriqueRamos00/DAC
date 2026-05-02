db = db.getSiblingDB('bantads_auth');

db.usuarios.drop();

db.usuarios.insertMany([
    //CLIENTES
    {
        nome: "Catharyna",
        cpf: "12912861012",
        email: "cli1@bantads.com.br",
        senha: "8PgqjzcWYmB93ZM7/UGNCw==:vBJG389YrpkR7zu08qCdrv/f/CmhH6x8rXoZHaohuYs=",
        tipoUsuario: "CLIENTE"
    },
    {
        nome: "Cleuddônio",
        cpf: "09506382000",
        email: "cli2@bantads.com.br",
        senha: "r5eTmzlTp34gyz3tbZmuYw==:T/GfCwM++LnL+8Q8OWQphqMmINHC5jU1AYG/uyyVy6g=",
        tipoUsuario: "CLIENTE"
    },
    {
        nome: "Catianna",
        cpf: "85733854057",
        email: "cli3@bantads.com.br",
        senha: "pxId7AS2gh39oyx68B/4Xw==:Yv7Lx6wZb+VaH7TWqaL7Qu0hkeBBPsOJW+WSr/o2ddg=",
        tipoUsuario: "CLIENTE"
    },
    {
        nome: "Cutardo",
        cpf: "58872160006",
        email: "cli4@bantads.com.br",
        senha: "eSvH0lt9GThfGFhAPKPr7Q==:QgAj+eP9hK1TsgxpJ+m7r+clTT+f+DFLMr8BR7y9LE8=",
        tipoUsuario: "CLIENTE"
    },
    {
        nome: "Coândrya",
        cpf: "76179646090",
        email: "cli5@bantads.com.br",
        senha: "QUfF70Y+MAKZUKvxgA61xQ==:nwkP9NDyG7UqTmNe9UvmE6hSYNwmVuEjlZ6Dpht3Hi8=",
        tipoUsuario: "CLIENTE"
    },

    //GERENTES
    {
        nome: "Geniéve",
        cpf: "98574307084",
        email: "ger1@bantads.com.br",
        senha: "6UIKBNtuEETEpIBsAf9Zsg==:n/5x5sH24XVlpPjgdDzbRiNbyUlnKTZOHftVbNd2fuA=",
        tipoUsuario: "GERENTE"
    },
    {
        nome: "Godophredo",
        cpf: "64065268052",
        email: "ger2@bantads.com.br",
        senha: "nOf79j9LG7OySCFQOu87SA==:shUDkH74FeKaswXn2PgGAvZt3IDkwcmqt1OIpmZv4vQ=",
        tipoUsuario: "GERENTE"
    },
    {
        nome: "Gyândula",
        cpf: "23862179060",
        email: "ger3@bantads.com.br",
        senha: "bsjcXsmhpPL8iefXGu6OAw==:JScd0B+HdhKGcT/3wAc5mu/wPmf+OLe6sQD+uvoN7lc=",
        tipoUsuario: "GERENTE"
    },

    //ADMINISTRADOR
    {
        nome: "Adamântio",
        cpf: "40501740066",
        email: "adm1@bantads.com.br",
        senha: "C5VDUtN+Utlel9SPPX0aTw==:IzDCdPsYZRsLsre40EARmc5aK3IUhO/UFQZfyncYRL4=",
        tipoUsuario: "ADMINISTRADOR"
    }
]);

// índices para performance
db.usuarios.createIndex({ email: 1 }, { unique: true });
db.usuarios.createIndex({ cpf: 1 }, { unique: true });

print("=== BANTADS Auth: Seed concluído com sucesso! ===");
print("Total de usuários inseridos: " + db.usuarios.countDocuments());