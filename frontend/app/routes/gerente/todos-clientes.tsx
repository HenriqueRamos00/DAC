import type { Route } from "./+types/todos-clientes";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { Users } from "lucide-react";
import { TabelaClientes } from "~/features/tabela-clientes/tabela-clientes";
import type { Cliente } from "~/models/dto/Cliente";
import { Input } from "~/components/ui/input";
import { Search } from "lucide-react";
import { useState } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Clientes" },
    { name: "description", content: "Todos os clientes" },
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

export default function TodosClientes({ loaderData }: Route.ComponentProps) {
  const { clientes } = loaderData;
  const [busca, setBusca] = useState("");

  const clientesFiltrados = clientes.filter((cliente) => {
    if (!busca) return true;
    const termo = busca.toLowerCase();
    return (
      cliente.nome.toLowerCase().includes(termo) ||
      cliente.cpf.includes(termo)
    );
  });

  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente", href: "/gerente" },
          { label: "Todos os Clientes" },
        ]}
      />

      <h1 className="text-sm text-primary uppercase">TODOS OS CLIENTES</h1>

      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
        <div className="border-b border-border pb-4">
          <span className="text-primary uppercase font-pixel flex items-center gap-2">
            <Users size={14} /> LISTA DE CLIENTES
          </span>
        </div>

        <div className="relative">
          <Input
            placeholder="Buscar por nome ou CPF..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            className="pr-10"
          />
          <Search className="absolute right-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
        </div>

        <TabelaClientes clientes={clientesFiltrados} pageSize={8} />
      </div>
    </div>
  );
}
