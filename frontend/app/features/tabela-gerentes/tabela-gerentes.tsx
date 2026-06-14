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
            <span className="block max-w-32 truncate" title={row.getValue("nome")}>
                {row.getValue("nome")}
            </span>
        ),
    },
    {
        accessorKey: "email",
        header: "E-mail",
        cell: ({ row }) => (
            <span className="block max-w-40 truncate" title={row.getValue("email")}>
                {row.getValue("email")}
            </span>
        ),
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
        header: "Saldo +",
        cell: ({ row }) => (
            <span className="font-mono font-bold text-(--manager)">
                {getFormattedCurrency(row.getValue("totalSaldoPositivo"))}
            </span>
        ),
    },
    {
        accessorKey: "totalSaldoNegativo",
        header: "Saldo -",
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
    compact?: boolean;
}

export function TabelaGerentes({ gerentes, pageSize = 10, compact = false }: TabelaGerentesProps) {
    return <DataTable columns={columns} data={gerentes} pageSize={pageSize} compact={compact} />;
}
