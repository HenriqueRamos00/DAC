import type { ColumnDef } from "@tanstack/react-table";
import type { Cliente } from "~/models/dto/Cliente";
import { DataTable } from "~/components/data-table";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { Button } from "~/components/ui/button";
import { NavLink } from "react-router";

const columns: ColumnDef<Cliente>[] = [
  {
    accessorKey: "cpf",
    header: "CPF",
    cell: ({ row }) => (
      <span className="font-mono text-muted-foreground">
        {row.getValue("cpf")}
      </span>
    ),
  },
  {
    accessorKey: "nome",
    header: "Nome",
  },
  {
    accessorKey: "cidade",
    header: "Cidade",
  },
  {
    accessorKey: "estado",
    header: "UF",
  },
  {
    accessorKey: "saldo",
    header: "Saldo",
    cell: ({ row }) => {
      const saldo = parseFloat(row.getValue("saldo"));
      return (
        <span className={`font-mono font-bold ${saldo >= 0 ? "text-info" : "text-destructive"}`}>
          {getFormattedCurrency(saldo)}
        </span>
      );
    },
  },
  {
    accessorKey: "limite",
    header: "Limite",
    cell: ({ row }) => (
      <span className="font-mono">
        {getFormattedCurrency(row.getValue("limite"))}
      </span>
    ),
  },
  {
    id: "acoes",
    header: "Ações",
    cell: ({ row }) => (
      <NavLink to={`/gerente/consultar?cpf=${row.original.cpf}`}>
        <Button variant="outline" size="sm">
          Detalhes
        </Button>
      </NavLink>
    ),
  },
];

interface TabelaClientesProps {
  clientes: Cliente[];
  pageSize?: number;
}

export function TabelaClientes({
  clientes,
  pageSize = 10,
}: TabelaClientesProps) {
  return (
    <DataTable
      columns={columns}
      data={clientes}
      pageSize={pageSize}
    />
  );
}
