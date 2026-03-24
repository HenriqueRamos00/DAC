import type { Route } from "./+types/top3";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { Trophy } from "lucide-react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Top 3 Clientes" },
    { name: "description", content: "Top 3 clientes por saldo" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { apiClient, cpf } = await getSessionAutenticada(request);
  const response = await apiClient.get(`/gerentes/${cpf}/clientes/top3`);

  if (!response.ok) {
    throw new Response("Erro ao carregar top 3 clientes", { status: response.status });
  }

  const clientes = await response.json() as Cliente[];

  return { clientes };
}

const medalColors = ["text-yellow-400", "text-gray-400", "text-amber-600"];
const medalLabels = ["1º", "2º", "3º"];

export default function Top3({ loaderData }: Route.ComponentProps) {
  const { clientes } = loaderData;
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Dashboard", href: "/gerente" },
          { label: "Top 3 Clientes" },
        ]}
      />
      <div className="flex flex-col mb-6">
        <h1 className="text-primary text-lg font-mono font-bold">Top 3 Clientes</h1>
        <span className="text-xs text-muted-foreground">Os três clientes com maior saldo.</span>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {clientes.map((cliente, index) => (
          <div key={cliente.cpf} className="flex flex-col bg-sidebar border-6 p-4 gap-3">
            <div className={`flex items-center gap-2 ${medalColors[index] ?? "text-foreground"}`}>
              <Trophy size={20} />
              <span className="font-mono font-bold text-sm">{medalLabels[index]}</span>
            </div>
            <p className="font-mono font-bold">{cliente.nome}</p>
            <p className="text-xs text-muted-foreground">{cliente.cpf}</p>
            <p className="text-xs text-muted-foreground">Conta: {cliente.conta}</p>
            <p className="text-primary font-mono font-bold text-sm">
              {getFormattedCurrency(parseFloat(cliente.saldo))}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
