import { getSession } from "~/auth/sessions.server";
import type { Route } from "./+types/depositar";
import { api } from "~/services/api.server";
import type { Saldo } from "~/models/dto/Saldo";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import { getFormattedCurrency, parseCurrency } from "~/lib/utils/formatCurrency";
import { ArrowDownFromLine, Play } from "lucide-react";
import { Label } from "~/components/ui/label";
import { Form, redirect } from "react-router";
import { getSessionAutenticada } from "~/services/auth.server";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Depositar" },
    { name: "description", content: "Depositar" },
  ];
}

export async function loader({ request } : Route.LoaderArgs) {
  const session = await getSession(request.headers.get("Cookie"))
  const conta = session.get("conta");

  const apiClient = api(request);

  if (!conta) {
    throw new Response("Conta não encontrada", { status: 404 })
  }
  const res = await apiClient.get(`/contas/${conta}/saldo`);

  if (!res.ok) {
    throw new Response("Erro ao obter o saldo", { status: 404 })
  }

  const saldo = await res.json() as Saldo;

  return { saldo }
}

export async function action({ request } : Route.ActionArgs) {
    const formData = await request.formData();
    const valor = formData.get("valor");
    const valorStr = typeof valor === "string" ? valor : "";
    if (valorStr === "") throw new Response("Valor Inválido", {status: 400})
    const valorFloat = parseCurrency(valorStr)

    const { apiClient, conta } = await getSessionAutenticada(request);
    const res = await apiClient.post(`/contas/${conta}/depositar`,
        {valor: valorFloat}
    )

    if (!res.ok) throw new Response("Operação Falhou", { status: res.status })

    return redirect("/cliente")
}

type FormData = {
    valor: string;
};
  
const EMPTY_FORM: FormData = {
    valor: "",
};

export default function Depositar( {loaderData} : Route.ComponentProps ) {
  const currencyRef = useCurrencyMask();
  const [form, setForm] = useState<FormData>({ valor: "" });
  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }
  const { saldo } = loaderData;

  return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Dashboard", href: "/cliente" },
                    { label: "Depósito" },
                ]}
            />

            <h1 className="text-sm text-primary uppercase">DEPOSITAR</h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary text-xs uppercase flex items-center gap-2"><ArrowDownFromLine size={14} /> REALIZAR DEPOSITO</span>
                </div>

                <div className="flex flex-col sm:flex-row gap-4">
                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-xs text-muted-foreground">Saldo</span>
                        <div className="text-lg text-primary">{getFormattedCurrency(saldo.saldo)}</div>
                    </div>
                </div>
                <Form method="post">
                    <div className="flex flex-col gap-2">
                        <Label className="text-muted-foreground uppercase font-mono text-xs">
                            VALOR DO DEPÓSITO (R$)
                        </Label>
                        <Input
                            ref={currencyRef}
                            id="valor"
                            name="valor"
                            inputMode="numeric"
                            placeholder="R$ 0,00"
                            value={form.valor}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="flex flex-col gap-4 mt-2">
                        <Button type="submit" variant="confirm" className="font-mono text-sm">
                            <Play className="size-2 fill-current" /> DEPOSITAR
                        </Button>
                    </div>
                </Form>
            </div>
        </div>
  )
}
