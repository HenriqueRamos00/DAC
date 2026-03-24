import Painel from "~/components/app-painel";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Mail, Phone, Trophy, User, Users, UserCheck } from "lucide-react";
import { useAuth } from "~/components/auth-provider";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Gerente } from "~/models/dto/Gerente";
import LinkRetro from "~/components/app-link-retro";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard Gerente" },
    { name: "description", content: "Tela inicial do gerente" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { apiClient, cpf } = await getSessionAutenticada(request);
  const response = await apiClient.get(`/gerentes/${cpf}`);

  if (!response.ok) {
    throw new Response("Erro ao carregar dashboard", { status: response.status });
  }

  const gerente = await response.json() as Gerente;

  return { gerente };
}

export default function Dashboard({ loaderData }: Route.ComponentProps) {
  const { auth } = useAuth();
  const { gerente } = loaderData;
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Dashboard" },
        ]}
      />
      <div className="flex flex-col">
        <div>
          Olá, <span className="text-(--manager)">{auth.nome ?? "Gerente"}</span>
        </div>
        <span className="text-xs">Bem-vindo ao seu painel gerencial.</span>
      </div>
      <div className="py-5 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        <Painel icon={User} title="Nome" content={gerente.nome} className="text-(--manager)" />
        <Painel icon={Mail} title="Email" content={gerente.email} className="text-chart-1" />
        <Painel icon={Phone} title="Telefone" content={gerente.telefone} className="text-chart-4" />
        <Painel icon={Users} title="Total de Clientes" content={String(gerente.total_clientes)} className="text-primary" />
      </div>
      <div className="py-5 grid grid-cols-1 md:grid-cols-3 gap-4">
        <LinkRetro link="/gerente/clientes" icon={Users} title="Todos os Clientes" className="text-(--manager)" />
        <LinkRetro link="/gerente/consultar" icon={UserCheck} title="Consultar Cliente" className="text-chart-1" />
        <LinkRetro link="/gerente/top3" icon={Trophy} title="Top 3 Clientes" className="text-primary" />
      </div>
    </div>
  );
}
