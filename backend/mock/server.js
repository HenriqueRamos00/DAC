const jsonServer = require("json-server");
const jwt = require("jsonwebtoken");
const fs = require("fs");

const server = jsonServer.create();
const router = jsonServer.router("db.json");
const middlewares = jsonServer.defaults();

const SECRET = "mock-secret";

server.use(middlewares);
server.use(jsonServer.bodyParser);

function getDb() {
  return router.db;
}

function seedDatabase() {
  const seed = JSON.parse(fs.readFileSync("./db.seed.json", "utf8"));
  router.db.setState(seed).write();
}

function findUserByLogin(login, senha) {
  return getDb()
    .get("auth")
    .find((u) => (u.email === login || u.cpf === login) && u.senha === senha)
    .value();
}

function authMiddleware(req, res, next) {
  const publicRoutes = [
    { method: "POST", path: "/login" },
    { method: "GET", path: "/reboot" },
    { method: "POST", path: "/clientes" }
  ];

  const isPublic = publicRoutes.some(
    (r) => r.method === req.method && r.path === req.path
  );

  if (isPublic) return next();

  const authHeader = req.headers.authorization || "";
  if (!authHeader.startsWith("Bearer ")) {
    return res.status(401).json({ message: "O usuário não está logado" });
  }

  const token = authHeader.replace("Bearer ", "");

  try {
    req.user = jwt.verify(token, SECRET);
    next();
  } catch {
    return res.status(401).json({ message: "Token inválido" });
  }
}

function getContaByNumero(numero) {
  return getDb().get("contas").find({ numero }).value();
}

function getContaByClienteCpf(clienteCpf) {
  return getDb().get("contas").find({ clienteCpf }).value();
}

function getClienteByCpf(cpf) {
  return getDb().get("clientes").find({ cpf }).value();
}

function getGerenteByCpf(cpf) {
  return getDb().get("gerentes").find({ cpf }).value();
}

function buildUsuario(user) {
  if (user.tipo === "CLIENTE") {
    return getClienteByCpf(user.cpf);
  }
  return getGerenteByCpf(user.cpf);
}

function calcularLimiteBase(salario) {
  return salario >= 2000 ? salario / 2 : 0;
}

function calcularNovoLimite(salario, saldoAtual) {
  const limiteBase = calcularLimiteBase(salario);

  if (saldoAtual < 0) {
    const saldoDevedor = Math.abs(saldoAtual);
    return Math.max(limiteBase, saldoDevedor);
  }

  return limiteBase;
}

function buildClienteCompleto(cliente) {
  const conta = getContaByClienteCpf(cliente.cpf);
  const gerente = conta ? getGerenteByCpf(conta.gerenteCpf) : null;

  return {
    cpf: cliente.cpf,
    nome: cliente.nome,
    telefone: cliente.telefone,
    email: cliente.email,
    endereco: cliente.endereco,
    CEP: cliente.CEP,
    cidade: cliente.cidade,
    estado: cliente.estado,
    salario: cliente.salario,
    conta: conta?.numero || null,
    saldo: conta?.saldo ?? null,
    limite: conta?.limite ?? null,
    gerente: gerente?.cpf || null,
    gerente_nome: gerente?.nome || null,
    gerente_email: gerente?.email || null
  };
}

// reboot
server.get("/reboot", (req, res) => {
  seedDatabase();
  res.status(200).json({ message: "Banco de dados reiniciado com sucesso" });
});

// login
server.post("/login", (req, res) => {
  const { login, senha } = req.body;

  const user = findUserByLogin(login, senha);

  if (!user) {
    return res.status(401).json({ message: "Usuário/Senha incorretos" });
  }

  const access_token = jwt.sign(
    {
      sub: user.cpf,
      email: user.email,
      tipo: user.tipo
    },
    SECRET,
    { expiresIn: "8h" }
  );

  res.json({
    access_token,
    token_type: "bearer",
    tipo: user.tipo,
    usuario: buildUsuario(user)
  });
});

// logout
server.post("/logout", authMiddleware,(req, res) => {
  res.json({
    cpf: req.user.sub,
    nome: "Logout efetuado",
    email: req.user.email,
    tipo: req.user.tipo
  });
});

server.use(authMiddleware);

// listar gerentes com resumo de clientes e saldos
server.get("/admin/dashboard", (req, res) => {
  const gerentes = getDb()
    .get("gerentes")
    .filter({ tipo: "GERENTE" })
    .value();

  const resumo = gerentes.map((gerente) => {
    const contas = getDb().get("contas").filter({ gerenteCpf: gerente.cpf }).value();

    let totalSaldoPositivo = 0;
    let totalSaldoNegativo = 0;

    for (const conta of contas) {
      if (conta.saldo >= 0) {
        totalSaldoPositivo += conta.saldo;
      } else {
        totalSaldoNegativo += conta.saldo;
      }
    }

    return {
      cpf: gerente.cpf,
      nome: gerente.nome,
      email: gerente.email,
      quantidadeClientes: contas.length,
      totalSaldoPositivo,
      totalSaldoNegativo,
    };
  });

  resumo.sort((a, b) => b.totalSaldoPositivo - a.totalSaldoPositivo);

  res.json(resumo);
});

// listar clientes de um gerente
server.get("/gerentes/:cpf/clientes", (req, res) => {
  const gerenteCpf = req.params.cpf;
  const contas = getDb().get("contas").filter({ gerenteCpf }).value();
  const clientes = contas
    .map((conta) => getClienteByCpf(conta.clienteCpf))
    .filter(Boolean)
    .map(buildClienteCompleto);
  res.json(clientes);
});

// listar clientes
server.get("/clientes", (req, res) => {
  const filtro = req.query.filtro;
  let clientes = getDb().get("clientes").value();

  if (filtro === "melhores_clientes") {
    const melhores = clientes
      .map(buildClienteCompleto)
      .sort((a, b) => (b.saldo ?? 0) - (a.saldo ?? 0))
      .slice(0, 3);

    return res.json(melhores);
  } else if (filtro == "para_aprovar") {
    clientes = getDb().get("clientes_para_aprov").value();
  }

  return res.json(clientes.map(buildClienteCompleto));
});

// cliente por cpf
server.get("/clientes/:cpf", (req, res) => {
  const cliente = getClienteByCpf(req.params.cpf);

  if (!cliente) {
    return res.status(404).json({ message: "Usuário não encontrado" });
  }

  res.json(buildClienteCompleto(cliente));
});

// atualizar cliente por cpf
server.put("/clientes/:cpf", (req, res) => {
  const cliente = getClienteByCpf(req.params.cpf);
  const conta = getContaByClienteCpf(req.params.cpf);

  if (!cliente) {
    return res.status(404).json({ message: "Usuário não encontrado" });
  }

  const novoSalario = Number(req.body.salario ?? cliente.salario);

  const updates = {
    nome: req.body.nome ?? cliente.nome,
    email: req.body.email ?? cliente.email,
    telefone: req.body.telefone ?? cliente.telefone,
    endereco: req.body.endereco ?? cliente.endereco,
    CEP: req.body.CEP ?? cliente.CEP,
    cidade: req.body.cidade ?? cliente.cidade,
    estado: req.body.estado ?? cliente.estado,
    salario: novoSalario,
  };

  getDb().get("clientes").find({ cpf: cliente.cpf }).assign(updates).write();

  if (conta) {
    getDb()
      .get("contas")
      .find({ clienteCpf: cliente.cpf })
      .assign({
        limite: calcularNovoLimite(novoSalario, conta.saldo),
      })
      .write();
  }

  getDb()
    .get("auth")
    .find({ cpf: cliente.cpf })
    .assign({
      nome: updates.nome,
      email: updates.email,
    })
    .write();

  const clienteAtualizado = getClienteByCpf(cliente.cpf);

  res.json(buildClienteCompleto(clienteAtualizado));
});

// aprovar cliente
server.post("/clientes/:cpf/aprovar", (req, res) => {
  const pendentes = getDb().get("clientes_para_aprov");
  const cliente = pendentes.find({ cpf: req.params.cpf }).value();

  if (!cliente) {
    return res.status(404).json({ message: "Usuário não encontrado" });
  }

  pendentes.remove({ cpf: req.params.cpf }).write();

  return res.status(200).json({
    message: "Cliente aprovado e removido da fila de aprovação",
    cliente,
  });
});

// rejeitar cliente
server.post("/clientes/:cpf/rejeitar", (req, res) => {
  const pendentes = getDb().get("clientes_para_aprov");
  const cliente = pendentes.find({ cpf: req.params.cpf }).value();

  if (!cliente) {
    return res.status(404).json({ message: "Usuário não encontrado" });
  }

  pendentes.remove({ cpf: req.params.cpf }).write();

  return res.status(200).json({
    message: "Cliente rejeitado e removido da fila de aprovação",
    cliente,
  });
});

// saldo
server.get("/contas/:numero/saldo", (req, res) => {
  const conta = getContaByNumero(req.params.numero);

  if (!conta) {
    return res.status(404).json({ message: "Conta não encontrada" });
  }

  res.json({
    cliente: conta.clienteCpf,
    conta: conta.numero,
    saldo: conta.saldo
  });
});

// extrato
server.get("/contas/:numero/extrato", (req, res) => {
  const conta = getContaByNumero(req.params.numero);

  if (!conta) {
    return res.status(404).json({ message: "Conta não encontrada" });
  }

  const movimentacoes = getDb()
    .get("movimentacoes")
    .filter(
      (m) => m.origem === conta.numero || m.destino === conta.numero
    )
    .value()
    .map((m) => ({
      data: m.data,
      tipo: m.tipo,
      origem: m.origem,
      destino: m.destino,
      valor: m.valor
    }));

  res.json({
    conta: conta.numero,
    saldo: conta.saldo,
    movimentacoes
  });
});

// depositar
server.post("/contas/:numero/depositar", (req, res) => {
  const conta = getContaByNumero(req.params.numero);
  const valor = Number(req.body.valor);

  if (!conta) {
    return res.status(404).json({ message: "Conta não encontrada" });
  }

  if (!valor || valor <= 0) {
    return res.status(400).json({ message: "Valor inválido" });
  }

  const novoSaldo = conta.saldo + valor;

  getDb().get("contas").find({ numero: conta.numero }).assign({ saldo: novoSaldo }).write();

  getDb().get("movimentacoes").push({
    id: Date.now(),
    data: new Date().toISOString(),
    tipo: "depósito",
    origem: null,
    destino: conta.numero,
    valor
  }).write();

  res.json({
    conta: conta.numero,
    data: new Date().toISOString(),
    saldo: novoSaldo
  });
});

// sacar
server.post("/contas/:numero/sacar", (req, res) => {
  const conta = getContaByNumero(req.params.numero);
  const valor = Number(req.body.valor);

  if (!conta) {
    return res.status(404).json({ message: "Conta não encontrada" });
  }

  if (!valor || valor <= 0) {
    return res.status(400).json({ message: "Valor inválido" });
  }

  if (conta.saldo - valor < -conta.limite) {
    return res.status(400).json({ message: "Saldo insuficiente" });
  }

  const novoSaldo = conta.saldo - valor;

  getDb().get("contas").find({ numero: conta.numero }).assign({ saldo: novoSaldo }).write();

  getDb().get("movimentacoes").push({
    id: Date.now(),
    data: new Date().toISOString(),
    tipo: "saque",
    origem: conta.numero,
    destino: null,
    valor
  }).write();

  res.json({
    conta: conta.numero,
    data: new Date().toISOString(),
    saldo: novoSaldo
  });
});

// transferir
server.post("/contas/:numero/transferir", (req, res) => {
  const origem = getContaByNumero(req.params.numero);
  const destino = getContaByNumero(req.body.destino);
  const valor = Number(req.body.valor);

  if (!origem || !destino) {
    return res.status(404).json({ message: "Conta de origem ou destino não encontrada" });
  }

  if (!valor || valor <= 0) {
    return res.status(400).json({ message: "Valor inválido" });
  }

  if (origem.saldo - valor < -origem.limite) {
    return res.status(400).json({ message: "Saldo insuficiente" });
  }

  const novoSaldoOrigem = origem.saldo - valor;
  const novoSaldoDestino = destino.saldo + valor;
  const data = new Date().toISOString();

  getDb().get("contas").find({ numero: origem.numero }).assign({ saldo: novoSaldoOrigem }).write();
  getDb().get("contas").find({ numero: destino.numero }).assign({ saldo: novoSaldoDestino }).write();

  getDb().get("movimentacoes").push({
    id: Date.now(),
    data,
    tipo: "transferência",
    origem: origem.numero,
    destino: destino.numero,
    valor
  }).write();

  res.json({
    conta: origem.numero,
    data,
    destino: destino.numero,
    saldo: novoSaldoOrigem,
    valor
  });
});

// criar gerente
server.post("/gerentes", (req, res) => {
  const { cpf, nome, email, senha } = req.body;

  if (!cpf || !nome || !email || !senha) {
    return res.status(400).json({ message: "Campos obrigatórios: cpf, nome, email, senha" });
  }

  const existe = getDb().get("gerentes").find({ cpf }).value();
  if (existe) {
    return res.status(409).json({ message: "CPF já cadastrado" });
  }

  const novoGerente = { cpf, nome, email, tipo: "GERENTE" };
  getDb().get("gerentes").push(novoGerente).write();
  getDb().get("auth").push({ cpf, email, senha, tipo: "GERENTE", nome }).write();

  res.status(201).json(novoGerente);
});

// atualizar gerente
server.put("/gerentes/:cpf", (req, res) => {
  const gerente = getGerenteByCpf(req.params.cpf);

  if (!gerente || gerente.tipo !== "GERENTE") {
    return res.status(404).json({ message: "Gerente não encontrado" });
  }

  const updates = {
    nome: req.body.nome ?? gerente.nome,
    email: req.body.email ?? gerente.email,
  };

  getDb().get("gerentes").find({ cpf: req.params.cpf }).assign(updates).write();

  const authUpdates = { nome: updates.nome, email: updates.email };
  if (req.body.senha) authUpdates.senha = req.body.senha;
  getDb().get("auth").find({ cpf: req.params.cpf }).assign(authUpdates).write();

  res.json({ ...gerente, ...updates });
});

// excluir gerente
server.delete("/gerentes/:cpf", (req, res) => {
  const gerente = getGerenteByCpf(req.params.cpf);

  if (!gerente || gerente.tipo !== "GERENTE") {
    return res.status(404).json({ message: "Gerente não encontrado" });
  }

  getDb().get("gerentes").remove({ cpf: req.params.cpf }).write();
  getDb().get("auth").remove({ cpf: req.params.cpf }).write();

  res.json({ message: "Gerente excluído com sucesso" });
});

server.listen(4010, () => {
  console.log("Mock API running on http://localhost:4010");
});
