import type { ColumnDef } from "@tanstack/react-table";
import { DataTable } from "~/components/data-table";
import { Button } from "~/components/ui/button";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import type { Cliente } from "~/models/dto/Cliente";

type TabelaClientesProps = {
  clientes: Cliente[];
  onAprovar?: (cliente: Cliente) => void;
  onRejeitar?: (cliente: Cliente) => void;
};

export function TabelaClientes({
  clientes,
  onAprovar,
  onRejeitar,
}: TabelaClientesProps) {
  const columns: ColumnDef<Cliente>[] = [
    { accessorKey: "nome", header: "Nome" },
    { accessorKey: "cpf", header: "CPF" },
    {
      accessorKey: "salario",
      header: "Salário",
      cell: ({ row }) => getFormattedCurrency(row.original.salario),
    },
    {
      id: "acoes",
      header: () => <div className="w-56 ml-auto text-center">Ações</div>,
      cell: ({ row }) => (
        <div className="w-56 ml-auto flex justify-center gap-2">
          <Button 
            variant="confirm"
            className="h-10 min-w-24 px-3"
            onClick={() => onAprovar?.(row.original)}>
            Aprovar
          </Button>
          <Button 
            variant="deny"
            className="h-10 min-w-24 px-3 sm:w-auto" 
            onClick={() => onRejeitar?.(row.original)}>
            Recusar
          </Button>
        </div>
      ),
    },
  ];

  return <DataTable columns={columns} data={clientes} pageSize={5} />;
}