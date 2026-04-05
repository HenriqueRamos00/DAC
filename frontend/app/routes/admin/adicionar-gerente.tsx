import { useEffect, useState } from "react";
import { Form, useActionData, useNavigation, data } from "react-router";
import { z } from "zod";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { getSessionAutenticada } from "~/services/auth.server";
import { GerenteForm, type GerenteFormErrors, type GerenteFormValues } from "~/features/gerente-form/gerente-form";
import type { Route } from "./+types/adicionar-gerente";

const gerenteSchema = z.object({
  nome: z.string().trim().min(1, "Nome obrigatório"),
  cpf: z.string().trim().min(11, "CPF inválido"),
  email: z.email("Email inválido"),
  telefone: z.string().trim().min(14, "Telefone obrigatório"),
  senha: z.string().trim().min(6, "A senha deve ter pelo menos 6 caracteres"),
});

type GerenteActionData = {
  errors?: GerenteFormErrors;
  formError?: string;
  success?: string;
};

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Novo Gerente" },
    { name: "description", content: "Tela de cadastro de novo gerente" }
  ];
}

export async function action({ request }: Route.ActionArgs) {
  const { apiClient } = await getSessionAutenticada(request);
  const formData = await request.formData();

  const rawValues = {
    nome: String(formData.get("nome") ?? ""),
    cpf: String(formData.get("cpf") ?? ""),
    email: String(formData.get("email") ?? ""),
    telefone: String(formData.get("telefone") ?? ""),
    senha: String(formData.get("senha") ?? ""),
  };

  const parsed = gerenteSchema.safeParse(rawValues);

  if (!parsed.success) {
    const fieldErrors = parsed.error.flatten().fieldErrors;
    return data<GerenteActionData>(
      {
        errors: {
          nome: fieldErrors.nome?.[0],
          cpf: fieldErrors.cpf?.[0],
          email: fieldErrors.email?.[0],
          telefone: fieldErrors.telefone?.[0],
          senha: fieldErrors.senha?.[0],
        },
      },
      { status: 400 }
    );
  }

  const response = await apiClient.post("/gerentes", parsed.data);

  if (!response.ok) {
    if (response.status === 409) {
      return data<GerenteActionData>(
        { formError: "Já existe um gerente com este CPF." },
        { status: 409 }
      );
    }

    return data<GerenteActionData>(
      { formError: "Não foi possível cadastrar o gerente." },
      { status: response.status }
    );
  }

  return data<GerenteActionData>(
    { success: "Gerente cadastrado com sucesso!" },
    { status: 200 }
  );
}

export default function NovoGerente() {
  const actionData = useActionData<typeof action>();
  const navigation = useNavigation();
  const isSubmitting = navigation.state === "submitting";

  const [form, setForm] = useState<GerenteFormValues>({
    nome: "",
    cpf: "",
    email: "",
    telefone: "",
    senha: "",
  });

  useEffect(() => {
    if (actionData?.success) {
      setForm({ nome: "", cpf: "", email: "", telefone: "", senha: "" });
    }
  }, [actionData?.success]);

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
          { label: "Gerentes", href: "/gerentes" },
          { label: "Novo Gerente" },
        ]}
      />

      <h1 className="text-sm text-primary uppercase">NOVO GERENTE</h1>

      <GerenteForm
        errors={actionData?.errors}
        form={form}
        formError={actionData?.formError}
        heading="Cadastrar gerente"
        isSubmitting={isSubmitting}
        onChange={handleInputChange}
        submitLabel="Salvar gerente"
        success={actionData?.success}
      />
    </div>
  );
}
