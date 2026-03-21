import { getSession } from "~/auth/sessions.server";
import type { Route } from "./+types/depositar";
import { api } from "~/services/api.server";
import type { Saldo } from "~/models/dto/Saldo";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";

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
    <div className="flex flex-col gap-6 uppercase font-mono font-bold text-xs">
      <AppBreadcrumb
          items={[
              { label: "Retro-Bank", href: "/" },
              { label: "Cliente", href: "/cliente" },
              { label: "Depositooo" },
          ]}
      />
      <h1 className="text-xl text-primary font-pixel uppercase">Depósito</h1>
            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary font-pixel uppercase">↓ REALIZAR DEPÓSITO</span>
                </div>

                <div className="flex flex-col sm:flex-row gap-4">
                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-sm text-muted-foreground">Saldo</span>
                        <div className="text-lg font-pixel text-primary">{getFormattedCurrency(saldo.saldo)}</div>
                    </div>
                </div>

                <div className="flex flex-col gap-2">
                    <label className="text-sm text-muted-foreground uppercase">
                        VALOR DO DEPÓSITO (R$)
                    </label>
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

                <div className="flex flex-col sm:flex-row gap-4 mt-2">
                    <Button type="button" variant="confirm" className="flex-1">
                        Depositar
                    </Button>
                </div>
            </div>
    </div>
  )
}
