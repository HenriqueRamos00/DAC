import Painel from "~/components/app-painel";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Upload, User, Wallet } from "lucide-react";
import { useAuth } from "~/components/auth-provider";
import { getSessionAutenticada } from "~/services/auth.server";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import type { Cliente } from "~/models/dto/Cliente";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard" },
    { name: "description", content: "Tela inicial do cliente" },
  ];
}

export async function loader({request} : Route.LoaderArgs) {
  const { apiClient, cpf } = await getSessionAutenticada(request);
  const response = await apiClient.get(`/clientes/${cpf}`);

  if (!response.ok) {
    throw new Response("Erro ao carregar dashboard", { status: response.status })
  }

  const cliente = await response.json() as Cliente;

  return { cliente }
}

export default function Dashboard( { loaderData } : Route.ComponentProps ) {
  const { auth } = useAuth()
  const { cliente } = loaderData
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Dashboard"}
        ]}
      />
      <div className="flex flex-col">
        <div>
          Olá, <span className="text-primary">{auth.nome ?? "Cliente"}</span>
        </div>
        <span className="text-xs">Bem-vindo ao seu painel bancário.</span>
      </div>
      <div className="flex justify-around py-5">
        <Painel icon={Wallet} title="Saldo atual" content={getFormattedCurrency(parseFloat(cliente.saldo))} color=""/>
        <Painel icon={Upload} title="Limite disponível" content={getFormattedCurrency(cliente.limite)} color=""/>
        <Painel icon={Wallet} title="Conta" content={`Nº ${cliente.conta}`} color=""/>
        <Painel icon={User} title="Gerente" content={cliente.gerente_nome} color=""/>{/*adicionar as cores dps*/}
      </div>
    </div>
  )
}
