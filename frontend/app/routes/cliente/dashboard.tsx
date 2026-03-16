import Painel from "~/components/app-painel";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Upload, User, Wallet } from "lucide-react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Dashboard" },
    { name: "description", content: "Tela inicial do cliente" },
  ];
}

const dados = {
  saldoAtual: "R$ 8.742,50"

}

export default function Dashboard() {
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
          Olá, <span className="text-primary">João da silva</span>
        </div>
        <span className="text-xs">Bem-vindo ao seu painel bancário.</span>
      </div>
      <div className="flex justify-around py-5">
        <Painel icon={Wallet} title="Saldo atual" content={dados.saldoAtual} color=""/>
        <Painel icon={Upload} title="Limite disponível" content={dados.saldoAtual} color=""/>
        <Painel icon={Wallet} title="Conta" content={dados.saldoAtual} color=""/>
        <Painel icon={User} title="Gerente" content={dados.saldoAtual} color=""/>{/*adicionar as cores dps*/}
      </div>
    </div>
  )
}
