import Painel from "~/components/app-painel";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Search, Upload, User, Wallet } from "lucide-react";
import type { Route } from "./+types/consultar-cliente";
import { data, Form, useNavigation } from "react-router";
import { FormField } from "~/components/form-field";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import UltimasMovimentacoes from "~/components/app-ultimas-movimentacoes";
import { useCpfMask } from "~/lib/pipe/cpf-mask";
import type { Cliente } from "~/models/dto/Cliente";
import type { Extrato } from "~/models/dto/Extrato";
import { api } from "~/services/api.server";

type ConsultarActionData = {
  cliente?: Cliente;
  extrato?: Extrato;
  formError?: string;
};

type DadoPessoalProps = {
  label: string;
  value: string;
  className?: string;
};

function DadoPessoal({ label, value, className }: DadoPessoalProps) {
  return (
    <div className={className}>
      <p className="text-xs text-muted-foreground">{label}</p>
      <p className="mt-1 bg-transparent py-2 text-xs">{value}</p>
    </div>
  );
}

function normalizeCpf(value: FormDataEntryValue | null): string {
  return String(value ?? "").replace(/\D/g, "");
}

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Consultar" },
    { name: "description", content: "Tela de consulta de clientes" },
  ];
}

export async function action({ request }: Route.ActionArgs) {
  const formData = await request.formData();
  const cpf = normalizeCpf(formData.get("cpf"));

  if (cpf.length !== 11) {
    return data<ConsultarActionData>(
      { formError: "Digite um CPF válido para consultar." },
      { status: 400 }
    );
  }

  const apiClient = api(request);
  const response = await apiClient.get(`/clientes/${cpf}`);

  if (!response.ok) {
    if (response.status === 404) {
      return data<ConsultarActionData>(
        { formError: "Cliente não encontrado." },
        { status: 404 }
      );
    }

    return data<ConsultarActionData>(
      { formError: "Nao foi possível carregar os dados do cliente." },
      { status: response.status }
    );
  }

  const cliente = (await response.json()) as Cliente;

  let extrato: Extrato = {
    conta: "",
    saldo: 0,
    movimentacoes: [],
  };

  if (cliente.conta) {
    const extratosResponse = await apiClient.get(`/contas/${cliente.conta}/extrato`);

    if (!extratosResponse.ok) {
      return data<ConsultarActionData>(
        { formError: "Nao foi possivel carregar as movimentacoes do cliente." },
        { status: extratosResponse.status }
      );
    }

    extrato = (await extratosResponse.json()) as Extrato;
  }

  return data<ConsultarActionData>({ cliente, extrato });
}

export default function Consultar({ actionData }: Route.ComponentProps) {
  const cliente = actionData?.cliente;
  const extrato = actionData?.extrato;
  const formError = actionData?.formError;
  const cpfRef = useCpfMask();
  const navigation = useNavigation();
  const isSubmitting = navigation.state === "submitting";

  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerente", href: "/gerente" },
          { label: "Consultar cliente" },
        ]}
      />
      <h3 className="uppercase">Consultar cliente</h3>

      <div className="mt-2 flex gap-2">
        <div className="min-w-0 flex-1 bg-sidebar border-6 p-2">
          <h3 className="border-b py-2 text-primary">Buscar cliente por CPF</h3>
          <Form method="post" className="py-5">
            <div className="grid grid-cols-1 items-center gap-4 md:grid-cols-2">
              <FormField label="CPF do cliente">
                <Input
                  ref={cpfRef}
                  id="cpf"
                  name="cpf"
                  inputMode="numeric"
                  placeholder="Digite o CPF"
                />
              </FormField>
              <Button
                type="submit"
                variant="confirm"
                className="mt-4 font-mono text-sm"
                disabled={isSubmitting}
              >
                <Search />
                Buscar
              </Button>
            </div>
            {formError ? (
              <p className="mt-4 text-sm text-destructive">{formError}</p>
            ) : null}
          </Form>
        </div>
      </div>

      {cliente && extrato ? (
        <>
          <div className="py-5 grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
            <Painel
              icon={Wallet}
              title="Saldo atual"
              content={getFormattedCurrency(parseFloat(cliente.saldo))}
              className="text-primary"
            />
            <Painel
              icon={Upload}
              title="Limite disponível"
              content={getFormattedCurrency(cliente.limite)}
              className="text-(--manager)"
            />
            <Painel
              icon={Wallet}
              title="Conta"
              content={`Nº ${cliente.conta}`}
              className="text-chart-1"
            />
            <Painel
              icon={User}
              title="Gerente"
              content={cliente.gerente_nome}
              className="text-chart-4"
            />
          </div>
          <div className="min-w-0 flex-1 bg-sidebar border-6 p-2">
            <div className="flex w-full items-center gap-4 border-b border-sidebar-border py-4 text-primary">
              <User size={20} className="" />
              <h3 className="uppercase font-mono font-bold">Dados Pessoais</h3>
            </div>
            <div className="grid grid-cols-1 gap-4 py-5 md:grid-cols-3">
              <DadoPessoal label="Nome" value={cliente.nome} />
              <DadoPessoal label="CPF" value={cliente.cpf} />
              <DadoPessoal label="Email" value={cliente.email} />
              <DadoPessoal label="Telefone" value={cliente.telefone} />
              <DadoPessoal label="Cidade" value={cliente.cidade} />
              <DadoPessoal label="UF" value={cliente.estado} />
              <DadoPessoal label="Salário" value={getFormattedCurrency(cliente.salario)} />
              <DadoPessoal label="Endereço" value={cliente.endereco} className="md:col-span-2" />
            </div>
          </div>
          <UltimasMovimentacoes mostrarBotaoExtratoCompleto={false} extrato={extrato} />
        </>
      ) : null}
    </div>
  );
}
