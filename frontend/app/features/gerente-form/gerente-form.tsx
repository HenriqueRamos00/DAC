import { Form } from "react-router";
import { Play, UserPlus } from "lucide-react";
import { FormField } from "~/components/form-field";
import { Button } from "~/components/ui/button";
import { Input } from "~/components/ui/input";
import { useCpfMask } from "~/lib/pipe/cpf-mask";
import { usePhoneMask } from "~/lib/pipe/phone-mask";
import { formatCpf } from "~/lib/utils";

export type GerenteFormValues = {
  nome: string;
  cpf: string;
  email: string;
  telefone: string;
  senha: string;
};

export type GerenteFormErrors = Partial<Record<keyof GerenteFormValues, string>>;

type GerenteFormProps = {
  errors?: GerenteFormErrors;
  form: GerenteFormValues;
  formError?: string;
  heading: string;
  isSubmitting: boolean;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  senhaRequired?: boolean;
  success?: string;
  submitLabel: string;
  cpfDisabled?: boolean;
};

export function GerenteForm({
  errors,
  form,
  formError,
  heading,
  isSubmitting,
  onChange,
  senhaRequired = true,
  success,
  submitLabel,
  cpfDisabled = false,
}: GerenteFormProps) {
  const cpfRef = useCpfMask();
  const phoneRef = usePhoneMask();

  return (
    <div className="max-w-3xl border-6 border-border bg-sidebar p-2">
      <h3 className="flex items-center gap-2 border-b border-border py-2 text-sm text-primary uppercase">
        <UserPlus size={16} /> {heading}
      </h3>

      <Form method="post" className="flex flex-col gap-4 py-5">
        <FormField label="Nome completo" htmlFor="nome" error={errors?.nome} required>
          <Input
            id="nome"
            name="nome"
            placeholder="Nome do gerente"
            value={form.nome}
            onChange={onChange}
          />
        </FormField>

        <FormField label="CPF" htmlFor="cpf" error={errors?.cpf} required={!cpfDisabled}>
          <Input
            ref={cpfDisabled ? undefined : cpfRef}
            id="cpf"
            name={cpfDisabled ? undefined : "cpf"}
            inputMode="numeric"
            placeholder="000.000.000-00"
            value={cpfDisabled ? formatCpf(form.cpf) : form.cpf}
            onChange={cpfDisabled ? undefined : onChange}
            readOnly={cpfDisabled}
            disabled={cpfDisabled}
          />
        </FormField>

        <FormField label="E-mail" htmlFor="email" error={errors?.email} required>
          <Input
            id="email"
            name="email"
            type="email"
            placeholder="gerente@retrobank.com"
            value={form.email}
            onChange={onChange}
          />
        </FormField>

        <FormField label="Telefone" htmlFor="telefone" error={errors?.telefone} required>
          <Input
            ref={phoneRef}
            id="telefone"
            name="telefone"
            inputMode="tel"
            placeholder="(00) 00000-0000"
            value={form.telefone}
            onChange={onChange}
          />
        </FormField>

        <FormField label="Senha" htmlFor="senha" error={errors?.senha} required={senhaRequired}>
          <Input
            id="senha"
            name="senha"
            type="password"
            placeholder="••••••••"
            value={form.senha}
            onChange={onChange}
          />
        </FormField>

        {formError ? (
          <p className="mt-2 text-sm text-destructive">{formError}</p>
        ) : null}

        {success ? (
          <p className="mt-2 text-sm text-primary">{success}</p>
        ) : null}

        <Button
          type="submit"
          variant="confirm"
          className="mt-4 flex h-12 w-full items-center justify-center gap-2 font-mono text-sm"
          disabled={isSubmitting}
        >
          <Play className="size-3 fill-current" /> {submitLabel}
        </Button>
      </Form>
    </div>
  );
}
