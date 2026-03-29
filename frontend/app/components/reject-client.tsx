import type { Cliente } from "~/models/dto/Cliente";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "./ui/dialog";
import { Button } from "./ui/button";
import { useState } from "react";
import { Label } from "./ui/label";

type DialogRejeitarClienteProps = {
    open: boolean;
    cliente: Cliente | null;
    onOpenChange: (open: boolean) => void;
    onConfirm: (motivo: string) => void;
    isLoading: boolean;
}

export function DialogRejeitarCliente({
  open,
  cliente,
  onOpenChange,
  onConfirm,
  isLoading,
}: DialogRejeitarClienteProps) {
    const [motivo, setMotivo] = useState("");
    function fechar() {
        onOpenChange(false);
        setMotivo("");
    }

    function confirmar() {
        onConfirm(motivo.trim());
        setMotivo("");
    }
    return (
        <Dialog open={open} onOpenChange={(v) => (!v ? fechar() : onOpenChange(v))}>
        <DialogContent className="max-w-md border-2 border-border bg-card p-0">
            <DialogHeader className="border-b border-border p-4">
            <DialogTitle className="text-sm uppercase text-primary">
                Rejeitar cliente
            </DialogTitle>

            <DialogDescription className="text-sm text-muted-foreground">
                Informe o motivo da rejeição.
            </DialogDescription>

            <p className="text-sm font-medium text-foreground">
                {cliente?.nome ?? "Cliente não selecionado"}
            </p>
            </DialogHeader>

            <div className="flex flex-col gap-2 p-4">
            <Label htmlFor="motivo-rejeicao" className="text-sm">
                Motivo
            </Label>
            <textarea
                disabled={isLoading}
                id="motivo-rejeicao"
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
                placeholder="Descreva o motivo da rejeição"
                className="w-full min-h-24 border border-border px-3 py-2 text-sm font-mono"
            />
            </div>

            <div className="flex justify-end gap-2 border-t p-4">
            <Button
                variant="outline"
                disabled={isLoading}
                className="h-9 px-4 text-xs uppercase"
                onClick={fechar}
            >
                Cancelar
            </Button>
            <Button
                variant="deny"
                className="h-9 px-4 text-xs uppercase sm:w-auto"
                onClick={confirmar}
                disabled={!motivo.trim() || isLoading}
            >
                Recusar
            </Button>
            </div>
        </DialogContent>
        </Dialog>
    );
}