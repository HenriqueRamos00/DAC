import type { Route } from "./+types/extrato";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useMemo, useState } from "react";
import { Button } from "~/components/ui/button";
import { Calendar } from "~/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "~/components/ui/popover";
import { format, isWithinInterval } from "date-fns";
import { ptBR } from "date-fns/locale";
import { CalendarIcon, FileText, Funnel, Play } from "lucide-react";
import type { DateRange } from "react-day-picker";
import type { ColumnDef } from "@tanstack/react-table";
import { Label } from "~/components/ui/label";
import { DataTable } from "~/features/data-table/data-table";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { api } from "~/services/api.server";
import { getSession } from "~/auth/sessions.server";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Extrato" },
    { name: "description", content: "Extrato" },
  ];
}

type MovimentacaoAPI = {
  id: number;
  data: string;
  tipo: string;
  origem: string | null;
  destino: string | null;
  valor: number;
};

type Movimentacao = {
  id: number;
  data: string;
  operacao: string;
  origemDestino: string;
  valor: number;
  tipo: "entrada" | "saida";
};

function mapOperacao(tipo: string): string {
  switch (tipo.toLowerCase()) {
    case "depósito": return "Depósito";
    case "saque": return "Saque";
    case "transferência": return "Transferência";
    default: return tipo;
  }
}

function mapMovimentacao(mov: MovimentacaoAPI, contaNumero: string): Movimentacao {
  const isEntrada = mov.destino === contaNumero;
  const tipo = isEntrada ? "entrada" as const : "saida" as const;

  let operacao = mapOperacao(mov.tipo);
  if (mov.tipo.toLowerCase() === "transferência") {
    operacao = isEntrada ? "Transferência recebida" : "Transferência enviada";
  }

  const origemDestino = mov.tipo.toLowerCase() === "transferência"
    ? (isEntrada ? mov.origem ?? "—" : mov.destino ?? "—")
    : "—";

  return {
    id: mov.id,
    data: format(new Date(mov.data), "yyyy-MM-dd HH:mm"),
    operacao,
    origemDestino,
    valor: mov.valor,
    tipo,
  };
}

export async function loader({ request }: Route.LoaderArgs) {
  const session = await getSession(request.headers.get("Cookie"));
  const token = session.get("token");
  const cpf = session.get("cpf");

  if (typeof token !== "string") {
    throw new Response("Unauthorized", { status: 401 });
  }

  const apiClient = api(request);

  const clienteRes = await apiClient.get(`/clientes/${cpf}`);
  if (!clienteRes.ok) {
    throw new Response("Erro ao carregar dados do cliente", { status: clienteRes.status });
  }

  const cliente = await clienteRes.json() as { conta: string };

  const extratoRes = await apiClient.get(`/contas/${cliente.conta}/extrato`);
  if (!extratoRes.ok) {
    throw new Response("Erro ao carregar extrato", { status: extratoRes.status });
  }

  const extrato = await extratoRes.json() as {
    conta: string;
    saldo: number;
    movimentacoes: MovimentacaoAPI[];
  };

  const movimentacoes = extrato.movimentacoes.map((m) =>
    mapMovimentacao(m, cliente.conta)
  );

  return { movimentacoes };
}

const columns: ColumnDef<Movimentacao>[] = [
  {
    accessorKey: "data",
    header: "Data/Hora",
    cell: ({ row }) => (
      <span className="text-muted-foreground font-mono">
        {row.getValue("data")}
      </span>
    ),
  },
  {
    accessorKey: "operacao",
    header: "Operação",
  },
  {
    accessorKey: "origemDestino",
    header: "Origem/Destino",
    cell: ({ row }) => (
      <span className="text-muted-foreground">
        {row.getValue("origemDestino")}
      </span>
    ),
  },
  {
    accessorKey: "valor",
    header: () => <div className="text-right">Valor</div>,
    cell: ({ row }) => {
      const mov = row.original;
      return (
        <div
          className={`text-right font-mono font-bold ${
            mov.tipo === "entrada" ? "text-info" : "text-destructive"
          }`}
        >
          {mov.tipo === "entrada" ? "+" : "-"}
          {getFormattedCurrency(mov.valor)}
        </div>
      );
    },
  },
];

export default function Extrato({ loaderData }: Route.ComponentProps) {
  const { movimentacoes } = loaderData;
  const [date, setDate] = useState<DateRange | undefined>();
  const [filtro, setFiltro] = useState<DateRange | undefined>();

  function handleFiltrar() {
    setFiltro(date);
  }

  function handleLimpar() {
    setDate(undefined);
    setFiltro(undefined);
  }

  const dadosFiltrados = useMemo(() => {
    if (!filtro?.from) return movimentacoes;

    return movimentacoes.filter((mov) => {
      const [datePart] = mov.data.split(" ");
      const [year, month, day] = datePart.split("-").map(Number);
      const movDate = new Date(year, month - 1, day);
      const from = filtro.from!;
      const to = filtro.to ?? filtro.from!;

      return isWithinInterval(movDate, {
        start: new Date(from.getFullYear(), from.getMonth(), from.getDate(), 0, 0, 0),
        end: new Date(to.getFullYear(), to.getMonth(), to.getDate(), 23, 59, 59),
      });
    });
  }, [filtro, movimentacoes]);

  const saldoPeriodo = useMemo(() => {
    return dadosFiltrados.reduce((acc, mov) => {
      return acc + (mov.tipo === "entrada" ? mov.valor : -mov.valor);
    }, 0);
  }, [dadosFiltrados]);

  return (
    <div className="flex flex-col gap-6">
      <AppBreadcrumb
        items={[
          { label: "Retro-Bank", href: "/" },
          { label: "Cliente", href: "/cliente" },
          { label: "Extrato" },
        ]}
      />

      <h1 className="text-sm text-primary uppercase">EXTRATO</h1>

      {/* Filtros */}
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
        <div className="border-b border-border pb-4">
          <span className="text-primary text-xs uppercase flex items-center gap-2">
            <Funnel size={14} /> FILTROS
          </span>
        </div>

        <Label className="text-muted-foreground uppercase font-mono text-xs">PERÍODO</Label>

        <div className="flex flex-row gap-4 items-center">
          <Popover>
            <PopoverTrigger
              render={
                <Button
                  variant="outline"
                  className="min-w-72 w-auto h-10 justify-start px-2.5 font-mono font-normal normal-case text-xs"
                />
              }
            >
              <CalendarIcon className="mr-2 size-4" />
              {date?.from ? (
                date.to ? (
                  <>
                    {format(date.from, "dd MMM yyyy", { locale: ptBR })} -{" "}
                    {format(date.to, "dd MMM yyyy", { locale: ptBR })}
                  </>
                ) : (
                  format(date.from, "dd MMM yyyy", { locale: ptBR })
                )
              ) : (
                <span className="text-muted-foreground">
                  Selecione um período
                </span>
              )}
            </PopoverTrigger>
            <PopoverContent className="w-auto p-0" align="start">
              <Calendar
                mode="range"
                defaultMonth={date?.from}
                selected={date}
                onSelect={setDate}
                numberOfMonths={2}
                locale={ptBR}
                className="[--cell-size:--spacing(10)]"
                classNames={{
                  weekday:
                    "flex-1 rounded-(--cell-radius) text-[0.65rem] font-mono font-normal text-muted-foreground select-none",
                  range_start: "bg-primary/30 rounded-l-md",
                  range_middle: "bg-primary/15 rounded-none",
                  range_end: "bg-primary/30 rounded-r-md",
                }}
              />
            </PopoverContent>
          </Popover>

          <Button
            type="button"
            variant="confirm"
            className="h-10 font-mono text-sm"
            onClick={handleFiltrar}
          >
            <Play className="size-2 fill-current" /> FILTRAR
          </Button>
          <Button
            type="button"
            variant="outline"
            className="h-10 font-mono text-sm"
            onClick={handleLimpar}
          >
            LIMPAR
          </Button>
        </div>
      </div>

      {/* Movimentações */}
      <div className="flex flex-col bg-card border-3 border-border p-4 gap-4">
        <div className="border-b border-border pb-4">
          <span className="text-primary text-xs uppercase flex items-center gap-2">
            <FileText size={14} /> MOVIMENTAÇÕES
          </span>
        </div>

        <DataTable columns={columns} data={dadosFiltrados} pageSize={10} />

        <div className="border-t-2 border-border pt-3 flex justify-between items-center">
          <span className="text-xs text-muted-foreground normal-case">
            Saldo consolidado no período:
          </span>
          <span className="text-sm text-primary">
            {getFormattedCurrency(saldoPeriodo)}
          </span>
        </div>
      </div>
    </div>
  );
}
