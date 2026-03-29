import type { Cliente } from "~/models/dto/Cliente";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "./ui/dialog";
import { Button } from "./ui/button";

type DialogAprovarClienteProps = {
    open: boolean;
    cliente: Cliente | null;
    onOpenChange: (open: boolean) => void;
    onConfirm: () => void;
    isLoading: boolean;
}

export function DialogAprovarCliente({
  open,
  cliente,
  onOpenChange,
  onConfirm,
  isLoading,
}: DialogAprovarClienteProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-md border-2 border-border bg-card p-0">
            <DialogHeader className="border-b border-border p-4">
                <DialogTitle className="text-sm uppercase text-primary">
                Aprovar cliente
                </DialogTitle>

                <DialogDescription className="text-sm text-muted-foreground">
                Deseja aprovar este cliente?
                </DialogDescription>

                <p className="text-sm font-medium text-foreground">
                {cliente?.nome ?? "Cliente não selecionado"}
                </p>
            </DialogHeader>

            <div className="flex justify-end gap-2 border-t border-border bg-muted/30 p-4"> 
                <Button 
                    variant="outline" 
                    disabled={isLoading}
                    className="h-9 px-4 text-xs uppercase" 
                    onClick={() => onOpenChange(false)} > 
                    Cancelar 
                </Button> 
                <Button 
                    variant="confirm" 
                    disabled={isLoading}
                    className="h-9 px-4 text-xs uppercase" 
                    onClick={onConfirm} > 
                    Aprovar 
                </Button> 
            </div>
        </DialogContent>
    </Dialog>
  );
}