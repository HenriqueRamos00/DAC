export type ContaMsResponse = {
  numeroConta: string;
  dataCriacao: string;
  saldo: number;
  limite: number;
  clienteNome: string;
  clienteCpf: string;
  gerenteCpf: string;
  gerenteNome: string;
  gerenteEmail: string;
};

export type ResumoContasGerenteMsResponse = {
  gerenteCpf: string;
  clientes: ContaMsResponse[];
  saldoPositivo: number;
  saldoNegativo: number;
};
