import { Input } from "~/components/ui/input";
import type { Route } from "./+types/transferir";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { ArrowLeftRight, Play } from "lucide-react"
import { Button } from "~/components/ui/button";
import { Label } from "~/components/ui/label";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Cliente } from "~/models/dto/Cliente";
import { getFormattedCurrency, parseCurrency } from "~/lib/utils/formatCurrency";
import { preventNegativeKey, preventNegativePaste } from "~/lib/utils/preventNegative";
import { data, Form, redirect } from "react-router";

export function meta({}: Route.MetaArgs) {
    return [{ title: "Transferência" }, { name: "description", content: "Tela de transferência do cliente" }];
}

type FormData = {
    contaDestino: string;
    valor: string;
};

const EMPTY_FORM: FormData = {
    contaDestino: "",
    valor: "",
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
    const contaDest = formData.get("contaDestino");

    const valorStr = typeof valor === "string" ? valor : "";
    if (valorStr === "") {
        return data({error: "Valor inválido"}, {status: 400});
    }
    const valorFloat = parseCurrency(valorStr)

    const contaDestStr = typeof contaDest === "string" ? contaDest : "";
    if (contaDestStr === "") {
        return data({error: "Conta inválida"}, {status: 400});
    }

    const { apiClient, conta } = await getSessionAutenticada(request);

    const res = await apiClient.post(`/contas/${conta}/transferir`,
        {   
            destino: contaDestStr,
            valor: valorFloat
        }
    );

    if (!res.ok) {
        return data({error: "Operação falhou"}, {status: res.status});
    }

    return redirect("/cliente");
}

export default function Transferencia({loaderData, actionData} : Route.ComponentProps) {
    const { saldoLimite } = loaderData;
    const currencyRef = useCurrencyMask();
    const [form, setForm] = useState<FormData>({ contaDestino: "", valor: "" });

    function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }

    return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Home", href: "/" },
                    { label: "Cliente", href: "/cliente" },
                    { label: "Transferência" },
                ]}
            />

            <h1 className="text-sm text-primary uppercase">TRANSFERÊNCIA</h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary text-xs uppercase flex items-center gap-2"><ArrowLeftRight size={14} /> NOVA TRANSFERÊNCIA</span>
                </div>

                <div className="bg-muted/30 border border-border p-4 flex flex-col gap-1">
                    <span className="text-xs text-muted-foreground">Valor disponível</span>
                    <div className="text-lg text-primary">{getFormattedCurrency(saldoLimite.disponivel)}</div>
                </div>
                <Form method="post">
                    <div className="flex flex-col gap-2">
                        <Label className="text-muted-foreground uppercase font-mono text-xs">
                            CONTA DESTINO
                        </Label>
                        <Input
                            id="contaDestino"
                            name="contaDestino"
                            placeholder="1234"
                            value={form.contaDestino}
                            onChange={handleChange}
                        />
                    </div>

                    <div className="flex flex-col gap-2">
                        <Label className="text-muted-foreground uppercase font-mono text-xs">
                            VALOR (R$)
                        </Label>
                        <Input
                            ref={currencyRef}
                            id="valor"
                            name="valor"
                            inputMode="numeric"
                            placeholder="0.00"
                            value={form.valor}
                            onChange={handleChange}
                            onKeyDown={preventNegativeKey}
                            onPaste={preventNegativePaste}
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
                            disabled={!form.valor.trim() || !form.contaDestino.trim()}
                            variant="confirm" 
                            className="font-mono text-sm">
                            <Play className="size-2 fill-current" /> CONFIRMAR
                        </Button>
                    </div>
                </Form>
            </div>
        </div>
    );
}