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
  },
  {
    accessorKey: "email",
    header: "E-mail",
  },
  {
    accessorKey: "salario",
    header: "Salário",
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
    header: "Conta",
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
    accessorKey: "gerente",
    header: "Gerente",
      cell: ({ row }) => {
        const { gerente: cpf_gerente, gerente_nome } = row.original;

        return (
          <div className="flex flex-col text-xs">
            <span>
              {gerente_nome}
            </span>
            <span>
              {formatCpf(cpf_gerente)}
            </span>
          </div>
        );
      }
  }
];

interface TabelaAdminClientesProps {
  clientes: Cliente[];
  pageSize?: number;
}

export function TabelaAdminClientes({
  clientes,
  pageSize = 10,
}: TabelaAdminClientesProps) {
  return (
    <DataTable
      columns={columns}
      data={clientes}
      pageSize={pageSize}
    />
  );
}
