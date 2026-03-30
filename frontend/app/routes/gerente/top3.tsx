import type { Route } from "./+types/top3";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { Trophy } from "lucide-react";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "~/components/ui/table";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Top 3 Clientes" },
    { name: "description", content: "Top 3 melhores clientes por saldo" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const response = await apiClient.get("/clientes?filtro=melhores_clientes");

  if (!response.ok) {
    throw new Response("Erro ao carregar top 3 clientes", { status: response.status });
  }

  const clientes = (await response.json()) as Cliente[];
  return { clientes };
}

const medalhas = ["🥇", "🥈", "🥉"];
const medalCores = ["text-yellow-400", "text-gray-300", "text-amber-600"];

function PodiumCard({
  cliente,
  posicao,
}: {
  cliente: Cliente;
  posicao: number;
}) {
  const sizeClasses = [
    "order-first md:order-2 md:-mt-8 py-10 px-8 text-base",
    "order-2 md:order-1 py-8 px-6 text-sm",
    "order-3 py-6 px-5 text-xs",
  ];

  return (
    <div
      className={`flex flex-col items-center gap-2 bg-card border-3 border-border ${sizeClasses[posicao]}`}
    >
      <span className={posicao === 0 ? "text-4xl" : posicao === 1 ? "text-3xl" : "text-2xl"}>{medalhas[posicao]}</span>
      <span className={`text-xs font-mono font-bold ${medalCores[posicao]}`}>
        #{posicao + 1}
      </span>
      <span className="text-sm font-bold">{cliente.nome}</span>
      <span className="text-xs text-muted-foreground font-mono">{cliente.cpf}</span>
      <span className="text-xs text-muted-foreground">
        {cliente.cidade} - {cliente.estado}
      </span>
      <span className="text-sm font-mono font-bold text-primary mt-2">
        {getFormattedCurrency(parseFloat(cliente.saldo))}
      </span>
    </div>
  );
}

export default function Top3({ loaderData }: Route.ComponentProps) {
  const { clientes } = loaderData;

  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente", href: "/gerente" },
          { label: "Top 3 Clientes" },
        ]}
      />

      <h1 className="text-sm text-primary uppercase flex items-center gap-2">
        <Trophy size={16} className="text-yellow-400" /> TOP 3 MELHORES CLIENTES
      </h1>

      {/* Podium */}
      <div className="flex flex-col md:flex-row justify-center items-end gap-4">
        {clientes.map((cliente, index) => (
          <PodiumCard key={cliente.cpf} cliente={cliente} posicao={index} />
        ))}
      </div>

      {/* Tabela */}
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
        <div className="border-b border-border pb-4">
          <span className="text-primary text-xs uppercase flex items-center gap-2">
            RANKING EM TABELA
          </span>
        </div>

        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>#</TableHead>
              <TableHead>Nome</TableHead>
              <TableHead>CPF</TableHead>
              <TableHead>Cidade</TableHead>
              <TableHead>UF</TableHead>
              <TableHead>Saldo</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody className="font-mono text-sm">
            {clientes.map((cliente, index) => (
              <TableRow key={cliente.cpf}>
                <TableCell>{medalhas[index]}</TableCell>
                <TableCell className="text-primary">{cliente.nome}</TableCell>
                <TableCell className="text-muted-foreground">{cliente.cpf}</TableCell>
                <TableCell>{cliente.cidade}</TableCell>
                <TableCell>{cliente.estado}</TableCell>
                <TableCell className="text-primary font-bold">
                  {getFormattedCurrency(parseFloat(cliente.saldo))}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
}
