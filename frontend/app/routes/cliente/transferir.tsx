import { Input } from "~/components/ui/input";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { ArrowLeftRight, Play } from "lucide-react"
import { Button } from "~/components/ui/button";
import { Label } from "~/components/ui/label";

export function meta({}: Route.MetaArgs) {
    return [{ title: "Transferência" }, { name: "description", content: "Tela de transferência do cliente" }];
}

type FormData = {
    contaDestino: string;
    valor: string;
};

const dados = {
    saldo: "R$ 8.742,50",
};

const EMPTY_FORM: FormData = {
    contaDestino: "",
    valor: "",
};

export default function Transferencia() {
    const currencyRef = useCurrencyMask();
    const [form, setForm] = useState<FormData>({ contaDestino: "", valor: "" });

    function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }

    return (
        <div className="flex flex-col gap-6">
            <AppBreadcrumb
                items={[
                    { label: "Retro-Bank", href: "/" },
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
                    <span className="text-xs text-muted-foreground">Saldo disponível</span>
                    <div className="text-lg text-primary">{dados.saldo}</div>
                </div>

                <div className="flex flex-col gap-2">
                    <Label className="text-muted-foreground uppercase font-mono text-xs">
                        CONTA DESTINO
                    </Label>
                    <Input
                        id="contaDestino"
                        name="contaDestino"
                        placeholder="00012345-6"
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
                    />
                </div>

                <div className="flex flex-col gap-4 mt-2">
                    <Button type="button" variant="confirm" className="font-mono text-sm">
                        <Play className="size-2 fill-current" /> CONFIRMAR
                    </Button>
                </div>
            </div>
        </div>
    );
}