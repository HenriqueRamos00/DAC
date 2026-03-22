import { Input } from "~/components/ui/input";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { Button } from "~/components/ui/button";

export function meta({}: Route.MetaArgs) {
    return [{ title: "Saque" }, { name: "description", content: "Tela de saque do cliente" }];
}

type FormData = {
    valor: string;
};

const dados = {
    saldo: "R$ 8.742,50",
    limite: "R$ 3.000,00",
    disponivel: "R$ 11.742,50",
};

export default function Saque() {
    const currencyRef = useCurrencyMask();
    const [form, setForm] = useState<FormData>({ valor: "" });
    function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
        setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }

    return (
        <div className="flex flex-col gap-6 uppercase font-mono font-bold text-xs">
            <AppBreadcrumb
                items={[
                    { label: "Retro-Bank", href: "/" },
                    { label: "Cliente", href: "/cliente" },
                    { label: "Saque" },
                ]}
            />

            <h1 className="text-xl text-primary font-pixel uppercase">SAQUE</h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary font-pixel uppercase">↑ REALIZAR SAQUE</span>
                </div>

                <div className="flex flex-col sm:flex-row gap-4">
                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-sm text-muted-foreground">Saldo</span>
                        <div className="text-lg font-pixel text-primary">{dados.saldo}</div>
                    </div>

                    <div className="flex-1 bg-muted/30 border border-border p-4 flex flex-col gap-1">
                        <span className="text-sm text-muted-foreground">Limite</span>
                        <div className="text-lg font-pixel text-cyan-400">{dados.limite}</div>
                    </div>
                </div>

                <div className="bg-blue-950/40 border border-blue-900/50 p-3 flex items-center">
                    <span className="text-blue-400">Disponível para saque: {dados.disponivel}</span>
                </div>

                <div className="flex flex-col gap-2">
                    <label className="text-sm text-muted-foreground uppercase">
                        VALOR DO SAQUE (R$)
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

                <div className="flex flex-col gap-4 mt-2">
                    <Button type="button" variant="confirm">
                        ► Sacar
                    </Button>
                </div>
            </div>
        </div>
    );
}