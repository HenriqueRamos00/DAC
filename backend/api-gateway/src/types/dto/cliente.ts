export type ClientResponseDto = {
  cpf: string;
  nome: string;
  telefone: string;
  email: string;
  cep: string;
  endereco: string;
  cidade: string;
  estado: string;
  salario: number;
  conta: string;
  saldo: number;
  limite: number;
  gerente: string;
  gerente_nome: string;
  gerente_email: string;
};

export type ClienteMsResponse = {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
  salario: number;
  endereco: string;
  CEP: string;
  cidade: string;
  estado: string;
};

export type ClienteGerenteResponseDto = {
  cpf: string;
  nome: string;
  email: string;
  telefone: string;
  endereco: string;
  cidade: string;
  estado: string;
  conta: string;
  saldo: number;
  limite: number;
};
