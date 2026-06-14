import type { ColumnDef } from "@tanstack/react-table";
import type { Cliente } from "~/models/dto/Cliente";
import { DataTable } from "~/components/data-table";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { formatCpf } from "~/lib/utils";

const columns: ColumnDef<Cliente>[] = [
  {
    accessorKey: "cpf",
    header: "CPF",
    cell: ({ row }) => (
      <span className="font-mono text-muted-foreground">
        {formatCpf(row.getValue("cpf"))}
      </span>
    ),
  },
  {
    accessorKey: "nome",
    header: "Nome",
    cell: ({ row }) => (
      <span className="block max-w-20 truncate" title={row.getValue("nome")}>
        {row.getValue("nome")}
      </span>
    ),
  },
  {
    accessorKey: "email",
    header: "E-mail",
    cell: ({ row }) => (
      <span className="block max-w-32 truncate" title={row.getValue("email")}>
        {row.getValue("email")}
      </span>
    ),
  },
  {
    accessorKey: "salario",
    header: "Sal.",
    cell: ({ row }) => {
      const salario = parseFloat(row.getValue("salario"));
      return (
        <span className="font-mono font-bold text-info">
          {getFormattedCurrency(salario)}
        </span>
      );
    },
  },
  {
    accessorKey: "conta",
    header: "Cta.",
  },
  {
    accessorKey: "saldo",
    header: "Saldo",
    cell: ({ row }) => {
      const saldo = parseFloat(row.getValue("saldo"));
      return (
        <span
          className={`font-mono font-bold ${saldo >= 0 ? "text-info" : "text-destructive"}`}
        >
          {getFormattedCurrency(saldo)}
        </span>
      );
    },
  },
  {
    accessorKey: "limite",
    header: "Lim.",
    cell: ({ row }) => (
      <span className="font-mono">
        {getFormattedCurrency(row.getValue("limite"))}
      </span>
    ),
  },
  {
    accessorKey: "gerente",
    header: "Ger.",
    cell: ({ row }) => {
      const { gerente: cpf_gerente, gerente_nome } = row.original;

      return (
        <div className="flex max-w-24 flex-col text-[11px]">
          <span className="truncate" title={gerente_nome}>
            {gerente_nome}
          </span>
          <span>
            {formatCpf(cpf_gerente)}
          </span>
        </div>
      );
    },
  },
];

interface TabelaAdminClientesProps {
  clientes: Cliente[];
  pageSize?: number;
  compact?: boolean;
}

export function TabelaAdminClientes({
  clientes,
  pageSize = 10,
  compact = false,
}: TabelaAdminClientesProps) {
  return (
    <DataTable
      columns={columns}
      data={clientes}
      pageSize={pageSize}
      compact={compact}
    />
  );
}
