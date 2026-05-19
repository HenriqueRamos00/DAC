export type GerenteMsResponse = {
  id: number;
  nome: string;
  email: string;
  cpf: string;
  telefone: string;
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