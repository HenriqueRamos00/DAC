import { useEffect, useState } from "react";
import { Form, useActionData, useNavigation, data } from "react-router";
import { z } from "zod";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { FormField } from "~/components/form-field";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import { usePhoneMask } from "~/lib/pipe/phone-mask";
import { Play, UserPlus } from "lucide-react";
import type { Route } from "./+types/dashboard";

const gerenteSchema = z.object({
  nome: z.string().trim().min(1, "Nome obrigatório"),
  cpf: z.string().trim().min(11, "CPF inválido"),
  email: z.email("Email inválido"),
  telefone: z.string().trim().min(14, "Telefone obrigatório"),
  senha: z.string().trim().min(6, "A senha deve ter pelo menos 6 caracteres"),
});

type GerenteFormValues = z.infer<typeof gerenteSchema>;

type GerenteActionData = {
  errors?: Partial<Record<keyof GerenteFormValues, string>>;
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

  // TODO: Integração com a API para salvar o gerente
  // const response = await apiClient.post("/gerentes", parsed.data);

  return data<GerenteActionData>(
    { success: "Gerente cadastrado com sucesso!" },
    { status: 200 }
  );
}

export default function NovoGerente() {
  const actionData = useActionData<typeof action>();
  const navigation = useNavigation();
  const phoneRef = usePhoneMask();
  
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

      <div className="bg-sidebar border-6 border-border p-2 max-w-3xl">
        <h3 className="border-b border-border py-2 text-primary uppercase flex items-center gap-2 text-sm">
          <UserPlus size={16} /> CADASTRAR GERENTE
        </h3>
        
        <Form method="post" className="py-5 flex flex-col gap-4">
          <FormField label="NOME COMPLETO" htmlFor="nome" error={actionData?.errors?.nome} required>
            <Input
              id="nome"
              name="nome"
              placeholder="Nome do gerente"
              value={form.nome}
              onChange={handleInputChange}
            />
          </FormField>

          <FormField label="CPF" htmlFor="cpf" error={actionData?.errors?.cpf} required>
            <Input
              id="cpf"
              name="cpf"
              inputMode="numeric"
              placeholder="000.000.000-00"
              value={form.cpf}
              onChange={handleInputChange}
            />
          </FormField>

          <FormField label="E-MAIL" htmlFor="email" error={actionData?.errors?.email} required>
            <Input
              id="email"
              name="email"
              type="email"
              placeholder="gerente@retrobank.com"
              value={form.email}
              onChange={handleInputChange}
            />
          </FormField>

          <FormField label="TELEFONE" htmlFor="telefone" error={actionData?.errors?.telefone} required>
            <Input
              ref={phoneRef}
              id="telefone"
              name="telefone"
              inputMode="tel"
              placeholder="(00) 00000-0000"
              value={form.telefone}
              onChange={handleInputChange}
            />
          </FormField>

          <FormField label="SENHA" htmlFor="senha" error={actionData?.errors?.senha} required>
            <Input
              id="senha"
              name="senha"
              type="password"
              placeholder="••••••••"
              value={form.senha}
              onChange={handleInputChange}
            />
          </FormField>

          {actionData?.formError ? (
            <p className="mt-2 text-sm text-destructive">{actionData.formError}</p>
          ) : null}
          
          {actionData?.success ? (
            <p className="mt-2 text-sm text-primary">{actionData.success}</p>
          ) : null}

          <Button
            type="submit"
            variant="confirm"
            className="mt-4 h-12 w-full font-mono text-sm flex items-center justify-center gap-2"
            disabled={isSubmitting}
          >
            <Play className="size-3 fill-current" /> SALVAR GERENTE
          </Button>
        </Form>
      </div>
    </div>
  );
}