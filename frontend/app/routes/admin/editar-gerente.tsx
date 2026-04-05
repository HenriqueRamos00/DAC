import { useEffect, useState } from "react";
import { data, useActionData, useNavigation } from "react-router";
import { z } from "zod";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { GerenteForm, type GerenteFormErrors, type GerenteFormValues } from "~/features/gerente-form/gerente-form";
import type { Gerente } from "~/models/dto/Gerente";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Route } from "./+types/editar-gerente";

const editarGerenteSchema = z.object({
  nome: z.string().trim().min(1, "Nome obrigatório"),
  email: z.email("Email inválido"),
  telefone: z.string().trim().min(14, "Telefone obrigatório"),
  senha: z.string().trim().min(6, "A senha deve ter pelo menos 6 caracteres").or(z.literal("")),
});

type EditarGerenteActionData = {
  errors?: Pick<GerenteFormErrors, "nome" | "email" | "telefone" | "senha">;
  formError?: string;
  success?: string;
};

function toFormValues(gerente: Gerente): GerenteFormValues {
  return {
    nome: gerente.nome,
    cpf: gerente.cpf,
    email: gerente.email,
    telefone: gerente.telefone,
    senha: "",
  };
}

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Editar gerente" },
    { name: "description", content: "Tela de edição de gerente" },
  ];
}

export async function loader({ params, request }: Route.LoaderArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const cpf = params.cpf;

  if (!cpf) {
    throw new Response("Gerente não informado", { status: 400 });
  }

  const response = await apiClient.get(`/gerentes/${cpf}`);

  if (!response.ok) {
    throw new Response("Erro ao carregar gerente", { status: response.status });
  }

  const gerente = (await response.json()) as Gerente;
  return { gerente };
}

export async function action({ params, request }: Route.ActionArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const cpf = params.cpf;

  if (!cpf) {
    throw new Response("Gerente não informado", { status: 400 });
  }

  const formData = await request.formData();
  const rawValues = {
    nome: String(formData.get("nome") ?? ""),
    email: String(formData.get("email") ?? ""),
    telefone: String(formData.get("telefone") ?? ""),
    senha: String(formData.get("senha") ?? ""),
  };

  const parsed = editarGerenteSchema.safeParse(rawValues);

  if (!parsed.success) {
    const fieldErrors = parsed.error.flatten().fieldErrors;
    return data<EditarGerenteActionData>(
      {
        errors: {
          nome: fieldErrors.nome?.[0],
          email: fieldErrors.email?.[0],
          telefone: fieldErrors.telefone?.[0],
          senha: fieldErrors.senha?.[0],
        },
      },
      { status: 400 }
    );
  }

  const payload: {
    nome: string;
    email: string;
    telefone: string;
    senha?: string;
  } = {
    nome: parsed.data.nome,
    email: parsed.data.email,
    telefone: parsed.data.telefone,
  };

  if (parsed.data.senha) {
    payload.senha = parsed.data.senha;
  }

  const response = await apiClient.put(`/gerentes/${cpf}`, payload);

  if (!response.ok) {
    return data<EditarGerenteActionData>(
      { formError: "Não foi possível salvar o gerente." },
      { status: response.status }
    );
  }

  return data<EditarGerenteActionData>({
    success: "Gerente atualizado com sucesso!",
  });
}

export default function EditarGerente({ loaderData }: Route.ComponentProps) {
  const { gerente } = loaderData;
  const actionData = useActionData<typeof action>();
  const navigation = useNavigation();
  const isSubmitting = navigation.state === "submitting";
  const [form, setForm] = useState<GerenteFormValues>(() => toFormValues(gerente));

  useEffect(() => {
    if (actionData?.errors || actionData?.formError) {
      return;
    }

    setForm(toFormValues(gerente));
  }, [actionData?.errors, actionData?.formError, gerente]);

  function handleInputChange(event: React.ChangeEvent<HTMLInputElement>) {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Home", href: "/" },
          { label: "Gerentes", href: "/admin/gerentes" },
          { label: "Editar gerente" },
        ]}
      />

      <h1 className="text-sm text-primary uppercase">Editar gerente</h1>

      <GerenteForm
        cpfDisabled
        errors={actionData?.errors}
        form={form}
        formError={actionData?.formError}
        heading="Editar gerente"
        isSubmitting={isSubmitting}
        onChange={handleInputChange}
        senhaRequired={false}
        submitLabel="Salvar alterações"
        success={actionData?.success}
      />
    </div>
  );
}
