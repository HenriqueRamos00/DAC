export type Movimentacao = {
  id: number;
  data: string;
  tipo: string;
  origem: string | null;
  destino: string | null;
  valor: number;
};

export type Extrato = {
  conta: string;
  saldo: number;
  movimentacoes: Movimentacao[];
};
