import { Input } from "~/components/ui/input";
import type { Route } from "./+types/sacar";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { Button } from "~/components/ui/button";
import { Label } from "~/components/ui/label";
import { ArrowUpFromLine, Play } from "lucide-react"
import { getSessionAutenticada } from "~/services/auth.server";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency, parseCurrency } from "~/lib/utils/formatCurrency";
import { data, Form, redirect } from "react-router";
import { preventNegativeKey, preventNegativePaste } from "~/lib/utils/preventNegative";

export function meta({}: Route.MetaArgs) {
    return [{ title: "Saque" }, { name: "description", content: "Tela de saque do cliente" }];
}

type FormData = {
    valor: string;
};

export async function loader({ request } : Route.LoaderArgs) {
    const { apiClient, cpf } = await getSessionAutenticada(request);
    const response = await apiClient.get(`/clientes/${cpf}`);

    if (!response.ok) {
        throw new Response("Erro ao carregar dados", { status: response.status });
    }

    const cliente = await response.json() as Cliente;

    const saldoLimite = {
        saldo: Number(cliente.saldo),
        limite: cliente.limite,
        disponivel: Number(cliente.saldo) + cliente.limite
    }

    return { saldoLimite };
}

export async function action({ request } : Route.ActionArgs) {
    const formData = await request.formData();
    const valor = formData.get("valor");
    const valorStr = typeof valor === "string" ? valor : "";
    if (valorStr === "") {
        return data({error: "Valor inválido"}, {status: 400});
    }
    const valorFloat = parseCurrency(valorStr)

    const { apiClient, conta } = await getSessionAutenticada(request);
    const res = await apiClient.post(`/contas/${conta}/sacar`,
        {valor: valorFloat}
    );

    if (!res.ok) {
        return data({error: "Operação falhou"}, {status: res.status});
    }

    return redirect("/cliente");
}

export default function Saque( {loaderData, actionData} : Route.ComponentProps ) {
    const { saldoLimite } = loaderData;
    const currencyRef = useCurrencyMask();
    const [form, setForm] = useState<FormData>({ valor: "" });
    function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }

    return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Cliente", href: "/cliente" },
                    { label: "Saque" },
                ]}
            />

            <h1 className="text-sm text-primary uppercase">SAQUE</h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary text-xs uppercase flex items-center gap-2"><ArrowUpFromLine size={14} /> REALIZAR SAQUE</span>
                </div>

                <div className="flex flex-col sm:flex-row gap-4">
                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-xs text-muted-foreground">Saldo</span>
                        <div className="text-lg text-primary">{getFormattedCurrency(saldoLimite.saldo)}</div>
                    </div>

                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-xs text-muted-foreground">Limite</span>
                        <div className="text-lg text-cyan-400">{getFormattedCurrency(saldoLimite.limite)}</div>
                    </div>
                </div>

                <div className="bg-blue-950/40 border border-blue-900/50 p-3 flex items-center">
                    <span className="text-blue-400">Disponível para saque: {getFormattedCurrency(saldoLimite.disponivel)}</span>
                </div>
                <Form method="post">
                    <div className="flex flex-col gap-2">
                        <Label className="text-muted-foreground uppercase font-mono text-xs">
                            VALOR DO SAQUE (R$)
                        </Label>
                        <Input
                            ref={currencyRef}
                            id="valor"
                            name="valor"
                            inputMode="numeric"
                            placeholder="R$ 0,00"
                            value={form.valor}
                            onChange={handleChange}
                            onPaste={preventNegativePaste}
                            onKeyDown={preventNegativeKey}
                        />
                    </div>

                    {actionData?.error ? (
                        <p id="saque-error" className="text-sm text-destructive">
                            {actionData.error}
                        </p>
                    ) : null}

                    <div className="flex flex-col gap-4 mt-2">
                        <Button 
                            type="submit" 
                            disabled={!form.valor.trim()}
                            variant="confirm" 
                            className="font-mono text-sm">
                            <Play className="size-2 fill-current" /> SACAR
                        </Button>
                    </div>
                </Form>
            </div>
        </div>
    );
}