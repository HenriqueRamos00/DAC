export type TipoTransacao = "depósito" | "saque" | "transferência"

export type Movimentacao = {
  id: number
  data: string
  tipo: TipoTransacao
  origem: string | null
  destino: string | null
  valor: string
  }

  export type Extrato = {
    conta: string
    saldo: number
    movimentacoes: Movimentacao[]
  }
