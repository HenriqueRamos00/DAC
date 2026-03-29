import { useEffect, useState } from "react";
import { data, Form, useActionData, useNavigation } from "react-router";
import { z } from "zod";
import Painel from "~/components/app-painel";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { FormField } from "~/components/form-field";
import { UfSelect, UF_OPTIONS } from "~/components/uf-select";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import type { Cliente } from "~/models/dto/Cliente";
import { api } from "~/services/api.server";
import { commitSession, getSession } from "~/auth/sessions.server";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { useCepMask } from "~/lib/pipe/cep-mask";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { usePhoneMask } from "~/lib/pipe/phone-mask";
import type { Route } from "./+types/perfil";
import { Shield, User, Wallet } from "lucide-react";

const estadoSchema = z.enum(UF_OPTIONS, {
  message: "UF inválida",
});

const perfilSchema = z.object({
  nome: z.string().trim().min(1, "Nome obrigatório"),
  email: z.email("Email inválido"),
  telefone: z.string().trim().min(14, "Telefone obrigatório"),
  endereco: z.string().trim().min(1, "Endereço obrigatório"),
  CEP: z.string().trim().refine((value) => value.replace(/\D/g, "").length === 8, {
    message: "CEP inválido",
  }),
  cidade: z.string().trim().min(1, "Cidade obrigatória"),
  estado: estadoSchema,
  salario: z.string().trim().min(1, "Salário obrigatório"),
});

type PerfilFormValues = z.infer<typeof perfilSchema>;
function normalizeEstado(value: string): PerfilFormValues["estado"] {
  const parsed = estadoSchema.safeParse(value);
  return parsed.success ? parsed.data : "PR";
}

type PerfilActionData = {
  errors?: Partial<Record<keyof PerfilFormValues, string>>;
  formError?: string;
  success?: string;
};

function getClienteOrThrow(request: Request) {
  return (async () => {
    const session = await getSession(request.headers.get("Cookie"));
    const token = session.get("token");
    const cpf = session.get("cpf");

    if (typeof token !== "string" || typeof cpf !== "string") {
      throw new Response("Unauthorized", { status: 401 });
    }

    const apiClient = api(request);
    const res = await apiClient.get(`/clientes/${cpf}`);

    if (!res.ok) {
      throw new Response("Erro ao carregar perfil", { status: res.status });
    }

    const cliente = (await res.json()) as Cliente;

    return { apiClient, cliente, cpf, session };
  })();
}

function toFormValues(cliente: Cliente): PerfilFormValues {
  return {
    nome: cliente.nome,
    email: cliente.email,
    telefone: cliente.telefone,
    endereco: cliente.endereco,
    CEP: formatCep(cliente.CEP),
    cidade: cliente.cidade,
    estado: normalizeEstado(cliente.estado),
    salario: getFormattedCurrency(cliente.salario),
  };
}

function parseCurrencyValue(value: string): number {
  const normalized = value
    .replace(/[^\d,.-]/g, "")
    .replace(/\./g, "")
    .replace(",", ".");

  return Number(normalized);
}

function formatCpf(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 11);

  if (digits.length !== 11) {
    return value;
  }

  return digits.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

function formatCep(value: string): string {
  const digits = value.replace(/\D/g, "").slice(0, 8);

  if (digits.length !== 8) {
    return value;
  }

  return digits.replace(/(\d{2})(\d{3})(\d{3})/, "$1.$2-$3");
}

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Perfil" },
    { name: "description", content: "Tela do perfil do cliente" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { cliente } = await getClienteOrThrow(request);

  return { cliente };
}

export async function action({ request }: Route.ActionArgs) {
  const { apiClient, cpf, session } = await getClienteOrThrow(request);
  const formData = await request.formData();

  const rawValues = {
    nome: String(formData.get("nome") ?? ""),
    email: String(formData.get("email") ?? ""),
    telefone: String(formData.get("telefone") ?? ""),
    endereco: String(formData.get("endereco") ?? ""),
    CEP: String(formData.get("CEP") ?? ""),
    cidade: String(formData.get("cidade") ?? ""),
    estado: String(formData.get("estado") ?? ""),
    salario: String(formData.get("salario") ?? ""),
  };

  const parsed = perfilSchema.safeParse(rawValues);

  if (!parsed.success) {
    const fieldErrors = parsed.error.flatten().fieldErrors;

    return data<PerfilActionData>(
      {
        errors: {
          nome: fieldErrors.nome?.[0],
          email: fieldErrors.email?.[0],
          telefone: fieldErrors.telefone?.[0],
          endereco: fieldErrors.endereco?.[0],
          CEP: fieldErrors.CEP?.[0],
          cidade: fieldErrors.cidade?.[0],
          estado: fieldErrors.estado?.[0],
          salario: fieldErrors.salario?.[0],
        },
      },
      { status: 400 }
    );
  }

  const salario = parseCurrencyValue(parsed.data.salario);

  if (!Number.isFinite(salario) || salario <= 0) {
    return data<PerfilActionData>(
      { errors: { salario: "Salário inválido" } },
      { status: 400 }
    );
  }

  const response = await apiClient.put(`/clientes/${cpf}`, {
    ...parsed.data,
    salario,
  });

  if (!response.ok) {
    return data<PerfilActionData>(
      { formError: "Não foi possível salvar o perfil." },
      { status: response.status }
    );
  }

  session.set("nome", parsed.data.nome);
  session.set("email", parsed.data.email);

  return data<PerfilActionData>(
    { success: "Perfil atualizado com sucesso." },
    {
      headers: {
        "Set-Cookie": await commitSession(session),
      },
    }
  );
}

export default function Perfil({ loaderData }: Route.ComponentProps) {
  const { cliente } = loaderData;
  const actionData = useActionData<typeof action>();
  const navigation = useNavigation();
  const cepRef = useCepMask();
  const phoneRef = usePhoneMask();
  const currencyRef = useCurrencyMask();
  const [form, setForm] = useState<PerfilFormValues>(() => toFormValues(cliente));
  const isSubmitting = navigation.state === "submitting";

  useEffect(() => {
    if (actionData?.errors || actionData?.formError) {
      return;
    }

    setForm(toFormValues(cliente));
  }, [actionData?.errors, actionData?.formError, cliente]);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target;
    const fieldName = name as keyof PerfilFormValues;

    setForm((current) => ({
      ...current,
      [fieldName]: value,
    }));
  }

  function handleEstadoChange(value: string) {
    const parsed = estadoSchema.safeParse(value);

    if (!parsed.success) {
      return;
    }

    setForm((current) => ({
      ...current,
      estado: parsed.data,
    }));
  }

  return (
    <div>
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Meu perfil" },
        ]}
      />
      <h3 className="uppercase">Alterar perfil</h3>
      <div className="flex gap-2">
        <div className="min-w-0 flex-1 bg-sidebar border-6 p-2">
          <h3 className="border-b py-2 text-primary uppercase">Dados Pessoais</h3>
          <Form method="post" className="py-5">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <FormField label="Nome" htmlFor="nome" error={actionData?.errors?.nome} required>
                <Input
                  id="nome"
                  name="nome"
                  placeholder="Digite o nome"
                  value={form.nome}
                  onChange={handleInputChange}
                />
              </FormField>
              <FormField label="CPF" htmlFor="cpf" required>
                <Input
                  id="cpf"
                  inputMode="numeric"
                  value={formatCpf(cliente.cpf)}
                  disabled
                  readOnly
                />
              </FormField>
              <FormField label="Email" htmlFor="email" error={actionData?.errors?.email} required>
                <Input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="Digite o email"
                  value={form.email}
                  onChange={handleInputChange}
                />
              </FormField>
              <FormField label="Telefone" htmlFor="telefone" error={actionData?.errors?.telefone} required>
                <Input
                  ref={phoneRef}
                  id="telefone"
                  name="telefone"
                  inputMode="tel"
                  value={form.telefone}
                  onChange={handleInputChange}
                />
              </FormField>
              <FormField
                label="Endereço"
                htmlFor="endereco"
                className="md:col-span-2"
                error={actionData?.errors?.endereco}
                required
              >
                <Input
                  id="endereco"
                  name="endereco"
                  placeholder="Digite o endereco"
                  value={form.endereco}
                  onChange={handleInputChange}
                />
              </FormField>
              <FormField label="CEP" htmlFor="CEP" error={actionData?.errors?.CEP} required>
                <Input
                  ref={cepRef}
                  id="CEP"
                  name="CEP"
                  inputMode="numeric"
                  placeholder="00.000-000"
                  value={form.CEP}
                  onChange={handleInputChange}
                />
              </FormField>
              <FormField label="Cidade" htmlFor="cidade" error={actionData?.errors?.cidade} required>
                <Input
                  id="cidade"
                  name="cidade"
                  value={form.cidade}
                  onChange={handleInputChange}
                />
              </FormField>
              <div className="flex flex-col gap-1">
                <UfSelect
                  id="estado"
                  name="estado"
                  label="Estado"
                  required
                  value={form.estado}
                  onValueChange={handleEstadoChange}
                />
                {actionData?.errors?.estado ? (
                  <p className="text-sm text-destructive">{actionData.errors.estado}</p>
                ) : null}
              </div>
              <FormField label="Salário" htmlFor="salario" error={actionData?.errors?.salario} required>
                <Input
                  ref={currencyRef}
                  id="salario"
                  name="salario"
                  inputMode="numeric"
                  value={form.salario}
                  onChange={handleInputChange}
                />
              </FormField>
            </div>
            {actionData?.formError ? (
              <p className="mt-4 text-sm text-destructive">{actionData.formError}</p>
            ) : null}
            {actionData?.success ? (
              <p className="mt-4 text-sm text-primary">{actionData.success}</p>
            ) : null}
            <Button
              type="submit"
              variant="confirm"
              className="mt-4 h-10 font-mono text-sm"
              disabled={isSubmitting}
            >
              SALVAR
            </Button>
          </Form>
        </div>

        <div className="flex flex-col gap-2">
          <Painel
            icon={Wallet}
            title="Saldo atual"
            content={getFormattedCurrency(parseFloat(cliente.saldo))}
            className="text-primary"
          />
          <Painel
            icon={Shield}
            title="Limite"
            content={getFormattedCurrency(cliente.limite)}
            className="text-(--manager)"
          />
          <Painel
            icon={User}
            title="Gerente responsável"
            content={cliente.gerente_nome}
            className="text-primary"
          />
        </div>
      </div>
    </div>
  );
}
