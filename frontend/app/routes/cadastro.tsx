import { data, useNavigate } from 'react-router';
import { useCallback } from 'react';
import { CrtMonitor } from '~/components/crt-monitor';
import { RegisterStepper } from '~/components/register-stepper';
import { parseCurrency } from '~/lib/utils/formatCurrency';
import { api } from '~/services/api.server';
import type { Route } from './+types/cadastro';

const MAX_SALARIO = 9999999999.99;
const SALARIO_INVALIDO = "Salário deve ser maior que zero e menor ou igual a R$ 9.999.999.999,99.";

function formValue(value: FormDataEntryValue | null) {
  return typeof value === "string" ? value.trim() : "";
}

function onlyDigits(value: FormDataEntryValue | null) {
  return formValue(value).replace(/\D/g, "");
}

async function getErrorMessage(response: Response) {
  try {
    const body = await response.json() as { message?: string; error?: string };
    return body.message || body.error || "Não foi possível concluir o cadastro.";
  } catch {
    return "Não foi possível concluir o cadastro.";
  }
}

export async function action({ request }: Route.ActionArgs) {
  const formData = await request.formData();
  const salario = parseCurrency(formValue(formData.get("salario")));

  if (!Number.isFinite(salario) || salario <= 0 || salario > MAX_SALARIO) {
    return data(
      { ok: false, error: SALARIO_INVALIDO },
      { status: 400 },
    );
  }

  const payload = {
    cpf: onlyDigits(formData.get("cpf")),
    nome: formValue(formData.get("nome")),
    email: formValue(formData.get("email")),
    telefone: onlyDigits(formData.get("telefone")),
    salario,
    cep: onlyDigits(formData.get("cep")),
    logradouro: formValue(formData.get("logradouro")),
    cidade: formValue(formData.get("cidade")),
    estado: formValue(formData.get("uf")).toUpperCase(),
    complemento: formValue(formData.get("complemento")),
    numero: formValue(formData.get("numero")),
  };

  const apiClient = api(request);
  const response = await apiClient.post("/clientes", payload);

  if (!response.ok) {
    return data(
      { ok: false, error: await getErrorMessage(response) },
      { status: response.status },
    );
  }

  return data(
    { ok: true, cliente: await response.json() },
    { status: 201 },
  );
}

export default function Autocadastro() {
  const navigate = useNavigate();
  const handleComplete = useCallback(() => {
    navigate("/");
  }, [navigate]);

  return (
    <div className="crt-page">
      <CrtMonitor title="">
        <RegisterStepper onComplete={handleComplete} />
      </CrtMonitor>
    </div>
  );
}
