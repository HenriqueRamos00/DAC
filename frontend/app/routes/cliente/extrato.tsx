import type { Route } from "./+types/extrato";
import { AppBreadcrumb } from "~/components/app-breadcrumb";
import { useState } from "react";
import { Button } from "~/components/ui/button";
import { Calendar } from "~/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "~/components/ui/popover";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";
import { CalendarIcon, FileText, Funnel, Play } from "lucide-react";
import type { DateRange } from "react-day-picker";
import type { ColumnFiltersState } from "@tanstack/react-table";
import { Label } from "~/components/ui/label";
import { getFormattedCurrency } from "~/lib/utils/formatCurrency";
import { getSessionAutenticada } from "~/services/auth.server";
import type { Extrato as ExtratoDTO } from "~/models/dto/Movimentacao";
import { TabelaMovimentacao } from "~/features/tabela-movimentacao/tabela-movimentacao";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Extrato" },
    { name: "description", content: "Extrato" },
  ];
}

export async function loader({ request }: Route.LoaderArgs) {
  const { apiClient, cpf } = await getSessionAutenticada(request);

  const clienteResponse = await apiClient.get(`/clientes/${cpf}`);
  if (!clienteResponse.ok) {
    throw new Response("Erro ao carregar dados do cliente", { status: clienteResponse.status });
  }

  const cliente = await clienteResponse.json() as { conta: string };

  const extratoResponse = await apiClient.get(`/contas/${cliente.conta}/extrato`);
  if (!extratoResponse.ok) {
    throw new Response("Erro ao carregar extrato", { status: extratoResponse.status });
  }

  const extrato = await extratoResponse.json() as ExtratoDTO;

  return { extrato, conta: cliente.conta };
}

export default function Extrato({ loaderData }: Route.ComponentProps) {
  const { extrato, conta } = loaderData;
  const [date, setDate] = useState<DateRange | undefined>();
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [saldoPeriodo, setSaldoPeriodo] = useState(0);

  function handleFiltrar() {
    setColumnFilters(date?.from ? [{ id: "data", value: date }] : []);
  }

  function handleLimpar() {
    setDate(undefined);
    setColumnFilters([]);
  }

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

        <TabelaMovimentacao
          movimentacoes={extrato.movimentacoes}
          conta={conta}
          pageSize={6}
          columnFilters={columnFilters}
          onSaldoChange={setSaldoPeriodo}
        />

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
