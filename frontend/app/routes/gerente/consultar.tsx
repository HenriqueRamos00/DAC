import type { Route } from "./+types/consultar";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import Painel from "~/components/app-painel";
import { Shield, User, Wallet } from "lucide-react";
import { Input } from "~/components/ui/input";
import { FormField } from "~/components/form-field";
import { UfSelect } from "~/components/uf-select";
import { data, Form } from "react-router";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Consultar Cliente" },
    { name: "description", content: "Consultar dados de um cliente" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const url = new URL(request.url);
  const cpfCliente = url.searchParams.get("cpf");

  if (!cpfCliente) {
    return { cliente: null };
  }

  const { apiClient } = await getSessionAutenticada(request);
  const response = await apiClient.get(`/clientes/${cpfCliente}`);

  if (!response.ok) {
    return data({ cliente: null, error: "Cliente não encontrado." }, { status: response.status });
  }

  const cliente = await response.json() as Cliente;

  return { cliente, error: null };
}

export default function Consultar({ loaderData }: Route.ComponentProps) {
  const { cliente, error } = loaderData as { cliente: Cliente | null; error?: string | null };
  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Dashboard", href: "/gerente" },
          { label: "Consultar Cliente" },
        ]}
      />
      <div className="flex flex-col mb-6">
        <h1 className="text-(--manager) text-lg font-mono font-bold">Consultar Cliente</h1>
        <span className="text-xs text-muted-foreground">Busque um cliente pelo CPF.</span>
      </div>
      <Form method="get" className="flex gap-2 mb-6">
        <Input
          name="cpf"
          placeholder="Digite o CPF do cliente"
          inputMode="numeric"
          defaultValue={cliente?.cpf ?? ""}
          className="max-w-xs"
        />
        <button type="submit" className="border-3 px-4 py-2 text-sm font-mono text-(--manager) border-(--manager) hover:bg-(--manager)/10">
          Buscar
        </button>
      </Form>

      {error && (
        <p className="text-destructive text-sm mb-4">{error}</p>
      )}

      {cliente && (
        <div className="flex gap-2">
          <div className="min-w-0 flex-1 bg-sidebar border-6 p-2">
            <h3 className="uppercase text-(--manager) border-b py-2">Dados do Cliente</h3>
            <div className="grid grid-cols-1 gap-4 py-5 md:grid-cols-2">
              <FormField label="nome" className="uppercase">
                <Input name="nome" value={cliente.nome} readOnly />
              </FormField>
              <FormField label="cpf" className="uppercase">
                <Input name="cpf" inputMode="numeric" value={cliente.cpf} readOnly />
              </FormField>
              <FormField label="email" className="uppercase">
                <Input name="email" type="email" value={cliente.email} readOnly />
              </FormField>
              <FormField label="telefone" className="uppercase">
                <Input name="telefone" inputMode="tel" value={cliente.telefone} readOnly />
              </FormField>
              <FormField label="endereco" className="uppercase col-span-2">
                <Input name="endereco" value={cliente.endereco} readOnly />
              </FormField>
              <FormField label="cidade" className="uppercase">
                <Input name="cidade" value={cliente.cidade} readOnly />
              </FormField>
              <UfSelect id="uf" value={cliente.estado} />
              <FormField label="salario" className="uppercase">
                <Input name="salario" inputMode="numeric" value={getFormattedCurrency(cliente.salario)} readOnly />
              </FormField>
            </div>
          </div>

          <div className="flex flex-col gap-4">
            <Painel icon={Wallet} title="Saldo atual" content={getFormattedCurrency(parseFloat(cliente.saldo))} className="text-primary" />
            <Painel icon={Shield} title="Limite" content={getFormattedCurrency(cliente.limite)} className="text-(--manager)" />
            <Painel icon={User} title="Conta" content={`Nº ${cliente.conta}`} className="text-chart-1" />
          </div>
        </div>
      )}
    </div>
  );
}
