export type GerenteResumo = {
  cpf: string
  nome: string
  email: string
  quantidadeClientes: number
  totalSaldoPositivo: number
  totalSaldoNegativo: number
}

export type GerenteDashboard = {
  gerente: {
    cpf: string
    nome: string
    email: string
    tipo: string
  }
  clientes: unknown[]
  saldo_positivo: number
  saldo_negativo: number
}
