import Painel from "~/components/app-painel";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Shield, User, Wallet } from "lucide-react";
import { api } from "~/services/api.server";
import { getSession } from "~/auth/sessions.server";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import type { Cliente } from "~/models/dto/Cliente";
import { Input } from "~/components/ui/input";
import { FormField } from "~/components/form-field";
import { Select } from "~/components/ui/select";
import { UfSelect } from "~/components/uf-select";


export function meta({}: Route.MetaArgs) {
  return [
    { title: "Perfil" },
    { name: "description", content: "Tela do perfil cliente" },
  ];
}

export async function loader({request} : Route.LoaderArgs) {
  const session = await getSession(request.headers.get("Cookie"))
  const token = session.get("token")
  const cpf = session.get("cpf")

  //Talvez mudar o retorno depois
  if (typeof token !== "string") {
    throw new Response("Unauthorized", { status: 401 })
  }

  const apiClient = api(request);
  const res = await apiClient.get(`/clientes/${cpf}`);

  if (!res.ok) {
    throw new Response("Erro ao carregar perfil", { status: res.status })
  }

  const cliente = await res.json() as Cliente;

  return { cliente }
}

export default function Perfil( { loaderData } : Route.ComponentProps ) {
  const { cliente } = loaderData
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Meu perfil"}
        ]}
      />
      <h3 className="uppercase">Alterar perfil</h3>
      <div className="flex gap-2">
        <div className="min-w-0 flex-1 bg-sidebar border-6 p-2">
          <h3 className="uppercase text-primary border-b py-2">Dados Pessoais</h3>
          <div className="grid grid-cols-1 gap-4 py-5 md:grid-cols-2">
            <FormField label="nome" className="uppercase">
              <Input
                  name="nome"
                  placeholder="Digite o nome"
                  value={cliente.nome}

              />
            </FormField>
            <FormField label="cpf" className="uppercase">
              <Input
                  name="cpf"
                  inputMode="numeric"
                  value={cliente.cpf}

              />
            </FormField>
            <FormField label="email" className="uppercase">
              <Input
                  name="email"
                  type="email"
                  placeholder="Digite o email"
                  value={cliente.email}

              />
            </FormField>
            <FormField label="telefone" className="uppercase">
              <Input
                  name="telefone"
                  inputMode="tel"
                  value={cliente.telefone}

              />
            </FormField>
            <FormField label="endereco" className="uppercase col-span-2">
              <Input
                  name="endereco"
                  placeholder="Digite o endereço"
                  value={cliente.endereco}

              />
            </FormField>
            <FormField label="cidade" className="uppercase">
              <Input
                  name="cidade"
                  value={cliente.cidade}
              />
            </FormField>
            <UfSelect
              id="uf"
              value={cliente.estado}
              // onValueChange={(value) =>
              //   // setForm((prev) => ({ ...prev, uf: value }))
              // }
            />
            <FormField label="salario" className="uppercase">
              <Input
                  name="salario"
                  inputMode="numeric"
                  value={getFormattedCurrency(cliente.salario)}
              />
            </FormField>
          </div>
          <button className="border-3">Colocar o botão bonito dps</button>
        </div>

        <div className="flex flex-col">
          <Painel icon={Wallet} title="Saldo atual" content={getFormattedCurrency(parseFloat(cliente.saldo))} color="text-primary"/>
          <Painel icon={Shield} title="Limite" content={getFormattedCurrency(cliente.limite)} color="text-[var(--manager)]"/>
          <Painel icon={User} title="Gerente responsável" content={cliente.gerente_nome} color="text-primary"/>
        </div>
      </div>
    </div>
  )
}
