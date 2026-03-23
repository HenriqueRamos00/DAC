import Painel from "~/components/app-painel";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { ArrowLeftRight, Download, FileText, Upload, User, Wallet } from "lucide-react";
import { useAuth } from "~/components/auth-provider";
import { getSessionAutenticada } from "~/services/auth.server";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import type { Cliente } from "~/models/dto/Cliente";
import LinkRetro from "~/components/app-link-retro";
import UltimasMovimentacoes from "~/components/app-ultimas-movimentacoes";
import type { Extrato } from "~/models/dto/Extrato";

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

  let extrato: Extrato = {
    conta: "",
    saldo: 0,
    movimentacoes: []
  }

  if (cliente.conta) {
    const extratosResponse = await  apiClient.get(`/contas/${cliente.conta}/extrato`)
    if (!extratosResponse.ok) {
      throw new Response("Erro ao carregar movimentações", { status: extratosResponse.status });
    }
    const extratoResponse = await extratosResponse.json() as Extrato
    extrato = extratoResponse
  }

  return { cliente, extrato }
}

export default function Dashboard( { loaderData } : Route.ComponentProps ) {
  const { auth } = useAuth()
  const { cliente, extrato } = loaderData
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
      <div className="py-5 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
        <Painel icon={Wallet} title="Saldo atual" content={getFormattedCurrency(parseFloat(cliente.saldo))} color="text-primary"/>
        <Painel icon={Upload} title="Limite disponível" content={getFormattedCurrency(cliente.limite)} color="text-[var(--manager)]"/>
        <Painel icon={Wallet} title="Conta" content={`Nº ${cliente.conta}`} color="text-chart-1"/>
        <Painel icon={User} title="Gerente" content={cliente.gerente_nome} color="text-chart-4"/>
      </div>
      <div className="py-5 grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-4">
        <LinkRetro link="/cliente/perfil" icon={User} title="Perfil" color="text-chart-4"/>
        <LinkRetro link="/cliente/deposito" icon={Download} title="Depositar" color="text-primary"/>
        <LinkRetro link="/cliente/saque" icon={Upload} title="Sacar" color="text-chart-3"/>
        <LinkRetro link="/cliente/transferencia" icon={ArrowLeftRight} title="Transferir" color="text-[var(--manager)]"/>
        <LinkRetro link="/cliente/extrato" icon={FileText} title="Extrato" color="text-chart-1"/>
      </div>
      <UltimasMovimentacoes extrato={extrato} />
    </div>
  )
}
