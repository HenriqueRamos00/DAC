import { useCallback } from "react";
import { endOfDay, format, isWithinInterval, startOfDay } from "date-fns";
import type { ColumnDef, ColumnFiltersState } from "@tanstack/react-table";
import type { DateRange } from "react-day-picker";
import type { Movimentacao } from "~/models/dto/Movimentacao";
import { DataTable } from "~/components/data-table";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";

function isEntrada(movimentacao: Movimentacao, conta: string) {
  return movimentacao.destino === conta;
}

function descricaoOperacao(movimentacao: Movimentacao, conta: string) {
  if (movimentacao.tipo !== "transferência") {
    return movimentacao.tipo;
  }
  return isEntrada(movimentacao, conta)
    ? "Transferência recebida"
    : "Transferência enviada";
}

function descricaoOrigemDestino(movimentacao: Movimentacao, conta: string) {
  if (movimentacao.tipo !== "transferência") return "—";
  return isEntrada(movimentacao, conta)
    ? (movimentacao.origem ?? "—")
    : (movimentacao.destino ?? "—");
}

function criarColunas(conta: string): ColumnDef<Movimentacao>[] {
  return [
    {
      accessorKey: "data",
      header: "Data/Hora",
      cell: ({ row }) => (
        <span className="text-muted-foreground font-mono">
          {format(new Date(row.getValue("data")), "yyyy-MM-dd HH:mm")}
        </span>
      ),
      filterFn: (row, _columnId, filterValue: DateRange) => {
        if (!filterValue?.from) return true;
        const dataMovimentacao = new Date(row.getValue("data") as string);
        const fim = filterValue.to ?? filterValue.from;
        return isWithinInterval(dataMovimentacao, {
          start: startOfDay(filterValue.from),
          end: endOfDay(fim),
        });
      },
    },
    {
      accessorKey: "tipo",
      header: "Operação",
      cell: ({ row }) => descricaoOperacao(row.original, conta),
    },
    {
      id: "origemDestino",
      header: "Origem/Destino",
      cell: ({ row }) => (
        <span className="text-muted-foreground">
          {descricaoOrigemDestino(row.original, conta)}
        </span>
      ),
    },
    {
      accessorKey: "valor",
      header: () => <div className="text-right">Valor</div>,
      cell: ({ row }) => {
        const movimentacao = row.original;
        const entrada = isEntrada(movimentacao, conta);
        return (
          <div
            className={`text-right font-mono font-bold ${
              entrada ? "text-info" : "text-destructive"
            }`}
          >
            {entrada ? "+" : "-"}
            {getFormattedCurrency(movimentacao.valor)}
          </div>
        );
      },
    },
  ];
}

interface TabelaMovimentacaoProps {
  movimentacoes: Movimentacao[];
  conta: string;
  pageSize?: number;
  columnFilters?: ColumnFiltersState;
  onSaldoChange?: (saldo: number) => void;
}

export function TabelaMovimentacao({
  movimentacoes,
  conta,
  pageSize = 10,
  columnFilters,
  onSaldoChange,
}: TabelaMovimentacaoProps) {
  const columns = criarColunas(conta);

  const handleFilteredRowsChange = useCallback(
    (rows: Movimentacao[]) => {
      if (!onSaldoChange) return;
      const saldo = rows.reduce(
        (acumulador, movimentacao) =>
          acumulador + (isEntrada(movimentacao, conta) ? movimentacao.valor : -movimentacao.valor),
        0
      );
      onSaldoChange(saldo);
    },
    [conta, onSaldoChange]
  );

  return (
    <DataTable
      columns={columns}
      data={movimentacoes}
      pageSize={pageSize}
      columnFilters={columnFilters}
      onFilteredRowsChange={handleFilteredRowsChange}
    />
  );
}
