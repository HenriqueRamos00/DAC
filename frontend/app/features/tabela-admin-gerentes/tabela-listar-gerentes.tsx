import { useState } from "react";
import type { ColumnDef } from "@tanstack/react-table";
import { PenSquare, Trash } from "lucide-react";
import { Form, NavLink, useNavigation } from "react-router";
import { DataTable } from "~/components/data-table";
import { Button } from "~/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "~/components/ui/dialog";
import { formatCpf } from "~/lib/utils";
import type { Gerente } from "~/models/dto/Gerente";

type DialogRemoverGerenteProps = {
  gerente: Gerente;
};

function DialogRemoverGerente({ gerente }: DialogRemoverGerenteProps) {
  const [open, setOpen] = useState(false);
  const navigation = useNavigation();
  const isSubmitting = navigation.state === "submitting";

  return (
    <>
      <Button
        variant="deny"
        className="h-9 px-4 text-xs uppercase sm:w-auto"
        onClick={() => setOpen(true)}
        disabled={isSubmitting}
      >
        <Trash /> Remover
      </Button>

      <Dialog open={open} onOpenChange={setOpen}>
        <DialogContent className="max-w-md border-2 border-border bg-card p-0">
          <DialogHeader className="border-b border-border p-4">
            <DialogTitle className="text-sm uppercase text-primary">
              Remover gerente
            </DialogTitle>

            <DialogDescription className="text-sm text-muted-foreground">
              Deseja remover este gerente?
            </DialogDescription>

            <p className="text-sm font-medium text-foreground">
              {gerente.nome}
            </p>
          </DialogHeader>

          <div className="border-t border-border bg-muted/30 p-4">
            <Form
              method="post"
              className="flex justify-end gap-2"
              onSubmit={() => setOpen(false)}
            >
              <input type="hidden" name="cpf" value={gerente.cpf} />

              <Button
                type="button"
                variant="outline"
                disabled={isSubmitting}
                className="h-9 px-4 text-xs uppercase"
                onClick={() => setOpen(false)}
              >
                Cancelar
              </Button>

              <Button
                type="submit"
                variant="deny"
                disabled={isSubmitting}
                className="h-9 px-4 text-xs uppercase sm:w-auto"
              >
                <Trash /> Confirmar
              </Button>
            </Form>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}

const columns: ColumnDef<Gerente>[] = [
  {
    accessorKey: "cpf",
    header: "CPF",
    cell: ({ row }) => (
      <span className="font-mono text-muted-foreground">
        {formatCpf(row.getValue("cpf"))}
      </span>
    ),
  },
  {
    accessorKey: "nome",
    header: "Nome",
  },
  {
    accessorKey: "email",
    header: "E-mail",
  },
  {
    accessorKey: "telefone",
    header: "Telefone",
  },
  {
    id: "acoes",
    header: "Ações",
    cell: ({ row }) => (
      <div className="flex gap-2">
        <NavLink to={`/admin/gerentes/${row.original.cpf}`}>
            <Button
                variant="outline"
                // disabled={isLoading}
                className="h-9 px-4 text-xs uppercase"
                // onClick={fechar}
            >
               <PenSquare/> Editar
            </Button>
        </NavLink>
        <DialogRemoverGerente key={row.original.cpf} gerente={row.original} />
      </div>
    ),
  },

];

interface TabelaAdminListarGerentesProps {
  clientes: Gerente[];
  pageSize?: number;
}

export function TabelaAdminListarGerentes({
  clientes,
  pageSize = 10,
}: TabelaAdminListarGerentesProps) {
  return (
    <DataTable
      columns={columns}
      data={clientes}
      pageSize={pageSize}
    />
  );
}
