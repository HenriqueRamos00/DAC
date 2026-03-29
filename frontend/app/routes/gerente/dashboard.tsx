import { getSessionAutenticada } from "~/services/auth.server";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import type { Cliente } from "~/models/dto/Cliente";
import { FileText } from "lucide-react";
import { TabelaClientes } from "~/features/tabela-clientes/tabela-clientes";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard Gerente" },
    { name: "description", content: "Painel do gerente" },
  ];
}

export async function loader({ request }:Route.LoaderArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const res = await apiClient.get("/clientes");

  if (!res.ok) {
    throw new Response("Erro ao obter informações do cliente", {status: res.status});
  }

  const clientes = (await res.json()) as Cliente[];
  return { clientes };
}

export default function GerenteDashboard({ loaderData } : Route.ComponentProps) {
  const { clientes } = loaderData
  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente" },
        ]}
      />
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4"> 
        <div className="border-b border-border pb-4"> 
          <span className="text-primary text-xs uppercase flex items-center gap-2"> Solicitações de Cadastro </span> 
        </div> 
            <TabelaClientes clientes={clientes} /> 
      </div>
    </div>
  );
}
