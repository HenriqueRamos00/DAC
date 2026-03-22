import { Input } from "~/components/ui/input";
import type { Route } from "./+types/dashboard";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useCurrencyMask } from "~/lib/pipe/currency-mask";
import { useState } from "react";
import { toast } from "sonner";
import { Button } from "~/components/ui/button";

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
        <div className="flex flex-col gap-6 uppercase font-mono font-bold text-xs">
            <AppBreadcrumb
                items={[
                    { label: "Retro-Bank", href: "/" },
                    { label: "Cliente", href: "/cliente" },
                    { label: "Transferência" },
                ]}
            />

            <h1 className="text-xl text-primary uppercase">TRANSFERÊNCIA</h1>

            <div className="flex flex-col bg-card border-3 border-border p-4 gap-4 max-w-3xl">
                <div className="border-b border-border pb-4">
                    <span className="text-primary uppercase font-pixel">NOVA TRANSFERÊNCIA</span>
                </div>

                <div className="bg-muted/30 border border-border p-4 flex flex-col gap-1">
                    <span className="text-sm text-muted-foreground">Saldo disponível</span>
                    <div className="text-lg font-pixel text-primary">{dados.saldo}</div>
                </div>

                <div className="flex flex-col gap-2">
                    <label className="text-sm text-muted-foreground uppercase">
                        CONTA DESTINO
                    </label>
                    <Input
                        id="contaDestino"
                        name="contaDestino"
                        placeholder="00012345-6"
                        value={form.contaDestino}
                        onChange={handleChange}
                    />
                </div>

                <div className="flex flex-col gap-2">
                    <label className="text-sm text-muted-foreground uppercase">
                        VALOR (R$)
                    </label>
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
                    <Button type="button" variant="confirm">
                        ► CONFIRMAR
                    </Button>
                </div>
            </div>
        </div>
    );
}