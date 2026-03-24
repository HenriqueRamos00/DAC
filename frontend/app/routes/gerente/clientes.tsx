import type { Route } from "./+types/clientes";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { DataTable } from "~/components/data-table";
import type { ColumnDef } from "@tanstack/react-table";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Todos os Clientes" },
    { name: "description", content: "Lista de todos os clientes do gerente" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { apiClient, cpf } = await getSessionAutenticada(request);
  const response = await apiClient.get(`/gerentes/${cpf}/clientes`);

  if (!response.ok) {
    throw new Response("Erro ao carregar clientes", { status: response.status });
  }

  const clientes = await response.json() as Cliente[];

  return { clientes };
}

const columns: ColumnDef<Cliente>[] = [
  {
    accessorKey: "nome",
    header: "Nome",
  },
  {
    accessorKey: "cpf",
    header: "CPF",
  },
  {
    accessorKey: "email",
    header: "Email",
  },
  {
    accessorKey: "conta",
    header: "Conta",
  },
  {
    accessorKey: "saldo",
    header: "Saldo",
    cell: ({ row }) => getFormattedCurrency(parseFloat(row.getValue("saldo"))),
  },
];

export default function Clientes({ loaderData }: Route.ComponentProps) {
  const { clientes } = loaderData;
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Dashboard", href: "/gerente" },
          { label: "Todos os Clientes" },
        ]}
      />
      <div className="flex flex-col mb-6">
        <h1 className="text-(--manager) text-lg font-mono font-bold">Todos os Clientes</h1>
        <span className="text-xs text-muted-foreground">Lista de todos os seus clientes.</span>
      </div>
      <DataTable columns={columns} data={clientes} />
    </div>
  );
}
