export type GerenteMsResponse = {
  id?: number;
  nome: string;
  email: string;
  cpf: string;
  telefone?: string;
  tipo?: 'GERENTE';
};

export type InserirGerenteRequest = {
  cpf: string;
  nome: string;
  email: string;
  senha: string;
  tipo: string;
};

export type InserirGerenteSagaResponse = {
  cpf: string;
  nome: string;
  email: string;
  tipo: string;
};

export type ContaGerenteMsResponse = {
  numeroConta: string;
  dataCriacao: string;
  saldo: number;
  limite: number;
  clienteNome: string;
  clienteCpf: string;
  gerenteCpf: string;
  gerenteNome: string;
};

export type ClienteDashboardResponse = {
  cliente: string;
  numero: string;
  saldo: number;
  limite: number;
  gerente: string;
  criacao: string;
};

export type ResumoContasGerenteMsResponse = {
  gerenteCpf: string;
  clientes: ContaGerenteMsResponse[];
  saldoPositivo: number;
  saldoNegativo: number;
};

export type GerenteDashboardResponse = {
  gerente: {
    cpf: string;
    nome: string;
    email: string;
    tipo: 'GERENTE';
  };
  clientes: ClienteDashboardResponse[];
  saldo_positivo: number;
  saldo_negativo: number;
};
