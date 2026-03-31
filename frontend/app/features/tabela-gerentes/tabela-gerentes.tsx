import type { ColumnDef } from "@tanstack/react-table";
import type { GerenteResumo } from "~/models/dto/GerenteResumo";
import { DataTable } from "~/components/data-table";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";

const columns: ColumnDef<GerenteResumo>[] = [
    {
        accessorKey: "cpf",
        header: "CPF",
        cell: ({ row }) => (
            <span className="font-mono text-muted-foreground">{row.getValue("cpf")}</span>
        ),
    },
    {
        accessorKey: "nome",
        header: "Nome",
        cell: ({ row }) => (
            <span>{row.getValue("nome")}</span>
        ),
    },
    {
        accessorKey: "email",
        header: "Email",
    },
    {
        accessorKey: "quantidadeClientes",
        header: "Clientes",
        cell: ({ row }) => (
            <span className="font-mono font-bold text-primary">{row.getValue("quantidadeClientes")}</span>
        ),
    },
    {
        accessorKey: "totalSaldoPositivo",
        header: "Total Positivo",
        cell: ({ row }) => (
            <span className="font-mono font-bold text-(--manager)">
                {getFormattedCurrency(row.getValue("totalSaldoPositivo"))}
            </span>
        ),
    },
    {
        accessorKey: "totalSaldoNegativo",
        header: "Total Negativo",
        cell: ({ row }) => {
            const valor: number = row.getValue("totalSaldoNegativo");
            return (
                <span className={`font-mono font-bold ${valor < 0 ? "text-destructive" : ""}`}>
                    {getFormattedCurrency(valor)}
                </span>
            );
        },
    },
];

interface TabelaGerentesProps {
    gerentes: GerenteResumo[];
    pageSize?: number;
}

export function TabelaGerentes({ gerentes, pageSize = 10 }: TabelaGerentesProps) {
    return <DataTable columns={columns} data={gerentes} pageSize={pageSize} />;
}
